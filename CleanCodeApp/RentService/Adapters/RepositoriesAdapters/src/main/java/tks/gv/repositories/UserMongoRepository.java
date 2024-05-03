package tks.gv.repositories;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.ClientException;
import tks.gv.exceptions.ClientLoginException;
import tks.gv.Client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserMongoRepository extends AbstractMongoRepository<ClientEntity> {

    static final String COLLECTION_NAME = "clients";

    @Autowired
    public UserMongoRepository(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        super(mongoClient, mongoDatabase);
        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains(COLLECTION_NAME);
        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                "$jsonSchema": {
                                    "bsonType": "object",
                                    "required": [
                                        "login"
                                    ],
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            getDatabase().createCollection(COLLECTION_NAME, createCollectionOptions);
        }
    }

    @Override
    protected MongoCollection<ClientEntity> getCollection() {
        return getDatabase().getCollection(COLLECTION_NAME, ClientEntity.class);
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }

    @Override
    public ClientEntity create(ClientEntity initUser) {
        if (initUser.getId() == null || initUser.getId().isBlank()) {
            initUser = new ClientEntity(
                    UUID.randomUUID().toString(),
                    initUser.getFirstName(),
                    initUser.getLastName(),
                    initUser.getLogin(),
                    initUser.getPassword(),
                    initUser.isArchive(),
                    initUser.getClientType()
            );
        }

        try {
            if (!read(Filters.eq("login", initUser.getLogin())).isEmpty()) {
                throw new ClientLoginException("Nie udalo sie zarejestrowac uzytkownika w bazie! - uzytkownik o tym loginie " +
                        "znajduje sie juz w bazie");
            }
            if (!this.getCollection().insertOne(initUser).wasAcknowledged()) {
                throw new ClientException("Nie udalo sie zarejestrowac uzytkownika w bazie! - brak odpowiedzi");
            }

        } catch (MongoWriteException e) {
            throw new MyMongoException(e.getMessage());
        }

        return initUser;
    }

    @Override
    public List<ClientEntity> read(Bson filter) {
        ///TODO refactor
        List<ClientEntity> list = new ArrayList<>();
        for (var doc : this.getDatabase().getCollection(COLLECTION_NAME, Document.class).find(filter).into(new ArrayList<>())) {
            list.add(new ClientEntity(
                            doc.getString("_id"),
                            doc.getString("firstname"),
                            doc.getString("lastname"),
                            doc.getString("login"),
                            doc.getString("password"),
                            doc.getBoolean("archive"),
                            doc.getString("clienttype")
                    )
            );

        }
        return list;
    }

    @Override
    public boolean updateByReplace(UUID uuid, ClientEntity user) {
        UpdateResult result = getCollection().replaceOne(Filters.eq("_id", uuid.toString()), user);

        return result.getModifiedCount() != 0;
    }

    @PostConstruct
    private void init() {
        destroy();

        create(ClientMapper.toUserEntity(new Client(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), "Johnny", "Brown", "login", "haselko", "normal")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), "Rose", "Tetris", "login15", "haselko", "athlete")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), "John", "Lee", "leeJo15", "haselko", "coach")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("3a722080-9668-42a2-9788-4695a4b9f5a7"), "Krzysztof", "Scala", "scKrzy", "haselko", "normal")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("126778af-0e19-46d4-b329-0b6b92548f9a"), "Adam", "Scout", "scAdam", "haselko", "normal")));
    }

    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
