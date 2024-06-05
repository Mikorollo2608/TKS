package tks.gv.auth.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.auth.repositories.data.UserEntity;

import java.util.ArrayList;

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

    public UserEntity findByLogin(String login) {
        System.out.println(getCollection().find().into(new ArrayList<>()).size());
        Bson filter = Filters.eq("login", login);
        return this.getCollection().find(filter).first();
    }

}
