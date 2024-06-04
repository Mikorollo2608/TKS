package tks.gv.userservice.repositories;

import com.mongodb.MongoWriteException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.ValidationOptions;

import com.mongodb.client.result.UpdateResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.bson.Document;
import org.bson.conversions.Bson;

import tks.gv.userservice.data.entities.AdminEntity;
import tks.gv.userservice.data.entities.ClientEntity;
import tks.gv.userservice.data.entities.UserEntity;
import tks.gv.userservice.data.entities.ResourceAdminEntity;

import tks.gv.userservice.data.mappers.entities.AdminMapper;
import tks.gv.userservice.data.mappers.entities.ClientMapper;

import tks.gv.userservice.data.mappers.entities.ResourceAdminMapper;
import tks.gv.userservice.exceptions.MyMongoException;

import tks.gv.userservice.exceptions.UnexpectedUserTypeException;
import tks.gv.userservice.exceptions.UserException;
import tks.gv.userservice.exceptions.UserLoginException;
import tks.gv.userservice.Admin;
import tks.gv.userservice.Client;
import tks.gv.userservice.ResourceAdmin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserMongoRepository implements AutoCloseable {
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    @Override
    public void close() {
        mongoClient.close();
    }


    static final String COLLECTION_NAME = "users";

    @Autowired
    public UserMongoRepository(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;

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

    protected MongoCollection<UserEntity> getCollection() {
        return getDatabase().getCollection(COLLECTION_NAME, UserEntity.class);
    }

    public String getCollectionName() {
        return COLLECTION_NAME;
    }

    public UserEntity create(UserEntity initUser) {
        if (initUser.getId() == null || initUser.getId().isBlank()) {
            if (initUser instanceof ClientEntity clientEntity) {
                initUser = new ClientEntity(
                        UUID.randomUUID().toString(),
                        clientEntity.getFirstName(),
                        clientEntity.getLastName(),
                        clientEntity.getLogin(),
                        clientEntity.getPassword(),
                        clientEntity.isArchive()
                );
            } else if (initUser instanceof AdminEntity adminEntity) {
                initUser = new AdminEntity(
                        UUID.randomUUID().toString(),
                        adminEntity.getFirstName(),
                        adminEntity.getLastName(),
                        adminEntity.getLogin(),
                        adminEntity.getPassword(),
                        adminEntity.isArchive()
                );
            } else if (initUser instanceof ResourceAdminEntity resourceAdminEntity) {
                initUser = new ResourceAdminEntity(
                        UUID.randomUUID().toString(),
                        resourceAdminEntity.getFirstName(),
                        resourceAdminEntity.getLastName(),
                        resourceAdminEntity.getLogin(),
                        resourceAdminEntity.getPassword(),
                        resourceAdminEntity.isArchive()
                );
            } else {
                throw new UnexpectedUserTypeException("Typ danego uzytkownika nie pasuje do zadnego z obslugiwanych!");
            }
        }

        try {
            if (!read(Filters.eq("login", initUser.getLogin())).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac uzytkownika w bazie! - uzytkownik o tym loginie " +
                        "znajduje sie juz w bazie");
            }
            if (!this.getCollection().insertOne(initUser).wasAcknowledged()) {
                throw new UserException("Nie udalo sie zarejestrowac uzytkownika w bazie! - brak odpowiedzi");
            }

        } catch (MongoWriteException e) {
            throw new MyMongoException(e.getMessage());
        }

        return initUser;
    }

    public List<UserEntity> read(Bson filter) {
        List<UserEntity> list = new ArrayList<>();
        String dispatcher;
        for (var doc : this.getDatabase().getCollection(COLLECTION_NAME, Document.class).find(filter).into(new ArrayList<>())) {
            dispatcher = doc.getString("_clazz");
            switch (dispatcher) {
                case "client" -> list.add(
                        new ClientEntity(
                                doc.getString("_id"),
                                doc.getString("firstname"),
                                doc.getString("lastname"),
                                doc.getString("login"),
                                doc.getString("password"),
                                doc.getBoolean("archive")
                        )
                );
                case "admin" -> list.add(
                        new AdminEntity(
                                doc.getString("_id"),
                                doc.getString("firstname"),
                                doc.getString("lastname"),
                                doc.getString("login"),
                                doc.getString("password"),
                                doc.getBoolean("archive")
                        )
                );
                case "resourceadmin" -> list.add(
                        new ResourceAdminEntity(
                                doc.getString("_id"),
                                doc.getString("firstname"),
                                doc.getString("lastname"),
                                doc.getString("login"),
                                doc.getString("password"),
                                doc.getBoolean("archive")
                        )
                );
            }
        }
        return list;
    }

    public List<UserEntity> readAll() {
        return this.read(Filters.empty());
    }

    public UserEntity readByUUID(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public boolean updateByReplace(UUID uuid, UserEntity user) {
        UpdateResult result = getCollection().replaceOne(Filters.eq("_id", uuid.toString()), user);

        return result.getModifiedCount() != 0;
    }

    public boolean update(UUID uuid, String fieldName, Object value) {
        if (fieldName.equals("_id")) {
            throw new MyMongoException("Proba zmiany UUID!");
        }
        Bson filter = Filters.eq("_id", uuid.toString());
        Bson setUpdate = Updates.set(fieldName, value);
        UpdateResult result = this.getCollection().updateOne(filter, setUpdate);
        return result.getModifiedCount() != 0;
    }

    public boolean delete(String login) {
        Bson filter = Filters.eq("login", login);
        var deletedObj = this.getCollection().findOneAndDelete(filter);
        return deletedObj != null;
    }

    @PostConstruct
    private void init() {
        destroy();

        create(ClientMapper.toUserEntity(new Client(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), "Johnny", "Brown", "login", "haselko")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), "Rose", "Tetris", "login15", "haselko")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), "John", "Lee", "leeJo15", "haselko")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("3a722080-9668-42a2-9788-4695a4b9f5a7"), "Krzysztof", "Scala", "scKrzy", "haselko")));
        create(ClientMapper.toUserEntity(new Client(UUID.fromString("126778af-0e19-46d4-b329-0b6b92548f9a"), "Adam", "Scout", "scAdam", "haselko")));

        create(AdminMapper.toUserEntity(new Admin(UUID.fromString("3b197615-6931-4aad-941a-44f78f527053"), "John", "Smith", "mainAdmin1@example", "haselko")));
        create(AdminMapper.toUserEntity(new Admin(UUID.fromString("4844c398-5cf1-44e0-a6d8-34c8a939d2ea"), "Eva", "Eve", "secondAdmin2@example", "haselko")));

        create(ResourceAdminMapper.toUserEntity(new ResourceAdmin(UUID.fromString("83b29a7a-aa96-4ff2-823d-f3d0d6372c94"), "Adam", "Key", "admRes1@test", "haselko")));
        create(ResourceAdminMapper.toUserEntity(new ResourceAdmin(UUID.fromString("a2f6cb49-5e9d-4069-ab91-f337224e833a"), "Henry", "Beer", "admRes2@test", "haselko")));
    }


    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
