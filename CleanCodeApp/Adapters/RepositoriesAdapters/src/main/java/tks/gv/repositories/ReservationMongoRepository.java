package tks.gv.repositories;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.entities.CourtEntity;
import tks.gv.data.entities.ReservationEntity;
import tks.gv.exceptions.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ReservationMongoRepository extends AbstractMongoRepository<ReservationEntity> {

    static final String COLLECTION_NAME = "reservations";

    public ReservationMongoRepository(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        super(mongoClient, mongoDatabase);

        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains(COLLECTION_NAME);
        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                "$jsonSchema": {
                                    "bsonType": "object",
                                    "required": [
                                        "clientid",
                                        "courtid",
                                        "begintime"
                                    ],
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            getDatabase().createCollection(COLLECTION_NAME, createCollectionOptions);
        }
    }

    //Checking database consistency
    @Override
    public ReservationEntity create(ReservationEntity initReservation) {
        try {
            //Check client
            var list1 = getDatabase().getCollection(UserMongoRepository.COLLECTION_NAME, ClientEntity.class)
                    .find(Filters.eq("_id", initReservation.getClientId())).into(new ArrayList<>());
            if (list1.isEmpty()) {
                throw new ReservationException("Brak podanego klienta w bazie!");
            }
            ClientEntity clientFound = list1.get(0);

            //Check court
            var list2 = getDatabase().getCollection(CourtMongoRepository.COLLECTION_NAME, CourtEntity.class)
                    .find(Filters.eq("_id", initReservation.getCourtId())).into(new ArrayList<>());
            if (list2.isEmpty()) {
                throw new ReservationException("Brak podanego boiska w bazie!");
            }
            CourtEntity courtFound = list2.get(0);

            if (courtFound.isRented() == 0 && !clientFound.isArchive() && !courtFound.isArchive()) {
                ReservationEntity newReservation = new ReservationEntity(UUID.randomUUID().toString(),
                        clientFound.getId(), courtFound.getId(), initReservation.getBeginTime(), null, 0);

                InsertOneResult result;
                ClientSession clientSession = getMongoClient().startSession();
                try {
                    clientSession.startTransaction();
                    result = this.getCollection().insertOne(clientSession, newReservation);
                    if (result.wasAcknowledged()) {
                        getDatabase().getCollection(CourtMongoRepository.COLLECTION_NAME, CourtEntity.class).updateOne(
                                clientSession,
                                Filters.eq("_id", courtFound.getId().toString()),
                                Updates.inc("rented", 1));
                    }
                    clientSession.commitTransaction();
                } catch (Exception e) {
                    clientSession.abortTransaction();
                    clientSession.close();
                    throw new MyMongoException(e.getMessage());
                } finally {
                    clientSession.close();
                }
                return result.wasAcknowledged() ? newReservation : null;
            } else if (clientFound.isArchive()) {
                throw new UserException("Nie udalo sie utworzyc rezerwacji - klient jest archiwalny!");
            } else if (courtFound.isArchive()) {
                throw new CourtException("Nie udalo sie utworzyc rezerwacji - boisko jest archiwalne!");
            } else {
                throw new MultiReservationException("To boisko jest aktualnie wypozyczone!");
            }
        } catch (MongoWriteException | MongoCommandException exception) {
            throw new MyMongoException(exception.getMessage());
        }
    }

    @Override
    public List<ReservationEntity> read(Bson filter) {
        return this.getCollection().find(filter).into(new ArrayList<>());
    }

    @Override
    public boolean updateByReplace(UUID uuid, ReservationEntity reservation) {
        Bson filter = Filters.eq("_id", uuid.toString());
        UpdateResult result = getCollection().replaceOne(filter, reservation);
        return result.getModifiedCount() != 0;
    }

    @Override
    public boolean delete(UUID uuid) {
        var res = readByUUID(uuid);
        if (res == null) {
            return true;
        }
        if (res.getEndTime() != null) {
            throw new IllegalStateException();
        }
        return super.delete(uuid);
    }

    @Override
    protected MongoCollection<ReservationEntity> getCollection() {
        return getDatabase().getCollection(getCollectionName(), ReservationEntity.class);
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }


    private boolean createNew(ReservationEntity dto) {
        InsertOneResult result;
        try {
            result = this.getCollection().insertOne(dto);
        } catch (MongoWriteException e) {
            throw new MyMongoException(e.getMessage());
        }
        return result.wasAcknowledged();
    }

    @PostConstruct
    private void init() {
        destroy();

        LocalDateTime dataStart = LocalDateTime.of(2023, Month.NOVEMBER, 30, 14, 20, 7, 200);
        LocalDateTime secondDate = LocalDateTime.of(2023, Month.NOVEMBER, 28, 14, 20, 1, 300);

       createNew(new ReservationEntity(UUID.randomUUID().toString(), UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157").toString(), UUID.fromString("fe6a35bb-7535-4c23-a259-a14ac0ccedba").toString(), dataStart , null, 0));
       createNew(new ReservationEntity(UUID.randomUUID().toString(), UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2").toString(), UUID.fromString("634d9130-0015-42bb-a70a-543dee846760").toString(), dataStart , dataStart.plusHours(1L), 100));
       createNew(new ReservationEntity(UUID.randomUUID().toString(), UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea").toString(), UUID.fromString("30ac2027-dcc8-4af7-920f-831b51023bc9").toString(), secondDate, null, 0));
    }

    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
