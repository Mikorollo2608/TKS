package tks.gv.repositories;

import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;
import tks.gv.Court;
import tks.gv.data.entities.CourtEntity;
import tks.gv.data.entities.ReservationEntity;
import tks.gv.data.mappers.entities.CourtMapper;
import tks.gv.exceptions.CourtException;
import tks.gv.exceptions.CourtNumberException;
import tks.gv.exceptions.MyMongoException;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CourtMongoRepository extends AbstractMongoRepository<CourtEntity> {

    static final String COLLECTION_NAME = "courts";

    public CourtMongoRepository(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        super(mongoClient, mongoDatabase);

        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains(COLLECTION_NAME);
        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                "$jsonSchema": {
                                    "bsonType": "object",
                                    "required": [
                                        "area",
                                        "basecost",
                                        "courtnumber",
                                        "rented"
                                    ],
                                    "properties": {
                                        "rented": {
                                            "bsonType": "int",
                                            "minimum": 0,
                                            "maximum": 1,
                                            "description": "Must be 1 for rented and 0 for available"
                                        }
                                    }
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            getDatabase().createCollection(COLLECTION_NAME, createCollectionOptions);
        }
    }

    @Override
    protected MongoCollection<CourtEntity> getCollection() {
        return getDatabase().getCollection(getCollectionName(), CourtEntity.class);
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }

    private boolean createNew(CourtEntity dto) {
        InsertOneResult result;
        try {
            result = this.getCollection().insertOne(dto);
        } catch (MongoWriteException e) {
            throw new MyMongoException(e.getMessage());
        }
        return result.wasAcknowledged();
    }

    @Override
    public CourtEntity create(CourtEntity court) {
        if (court.getId() == null || court.getId().isBlank()) {
            court = new CourtEntity(
                    UUID.randomUUID().toString(),
                    court.getArea(),
                    court.getBaseCost(),
                    court.getCourtNumber(),
                    court.isArchive(),
                    court.isRented()
            );
        }
        if (!read(Filters.eq("courtnumber", court.getCourtNumber())).isEmpty()) {
            throw new CourtNumberException("Nie udalo sie zarejestrowac boiska w bazie! - boisko o tym numerze " +
                    "znajduje sie juz w bazie");
        }
        if (!createNew(court)) {
            throw new CourtException("Nie udalo sie zarejestrowac boiska w bazie! - brak odpowiedzi");
        }
        return court;
    }

    @Override
    public List<CourtEntity> read(Bson filter) {
        return new ArrayList<>(this.getCollection().find(filter).into(new ArrayList<>()));
    }

    @Override
    public boolean updateByReplace(UUID uuid, CourtEntity court) {
        UpdateResult result = getCollection().replaceOne(Filters.eq("_id", uuid.toString()), court);
        return result.getModifiedCount() != 0;
    }

    @Override
    public boolean delete(UUID uuid) {
        Bson filter = Filters.eq("courtid", uuid.toString());
        ClientSession clientSession = getMongoClient().startSession();
        try {
            clientSession.startTransaction();
            var reservation = this.getDatabase().getCollection(ReservationMongoRepository.COLLECTION_NAME, ReservationEntity.class).find(filter).first();
            if (reservation != null) {
                throw new CourtException("Nie można usunąć boiska z którym powiązane jest wypożyczenie.");
            }
            boolean result = super.delete(uuid);
            if (result) {
                clientSession.commitTransaction();
            } else {
                clientSession.abortTransaction();
            }
            return result;
        } catch (Exception exception) {
            clientSession.abortTransaction();
            clientSession.close();
            if (exception instanceof IllegalStateException ise) {
                throw ise;
            }
            throw new MyMongoException(exception.getMessage());
        } finally {
            clientSession.close();
        }
    }

    @PostConstruct
    private void init() {
        destroy();

        createNew(CourtMapper.toMongoCourt(new Court(UUID.fromString("634d9130-0015-42bb-a70a-543dee846760"), 100, 100, 991)));
        createNew(CourtMapper.toMongoCourt(new Court(UUID.fromString("fe6a35bb-7535-4c23-a259-a14ac0ccedba"), 100, 200, 992)));
        update(UUID.fromString("fe6a35bb-7535-4c23-a259-a14ac0ccedba"), "rented", 1);
        createNew(CourtMapper.toMongoCourt(new Court(UUID.fromString("30ac2027-dcc8-4af7-920f-831b51023bc9"), 300, 200, 993)));
        update(UUID.fromString("30ac2027-dcc8-4af7-920f-831b51023bc9"), "rented", 1);
        createNew(CourtMapper.toMongoCourt(new Court(UUID.fromString("d820d682-0f5d-46b7-9963-66291e5f64b0"), 350, 100, 994)));
        createNew(CourtMapper.toMongoCourt(new Court(UUID.fromString("2e9258b2-98dd-4f9a-8f73-6f4f56c2e618"), 150, 200, 995)));
    }

    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
