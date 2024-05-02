package tks.gv.repositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.data.entities.Entity;

import tks.gv.exceptions.MyMongoException;

import java.util.List;
import java.util.UUID;

public abstract class AbstractMongoRepository<T> implements AutoCloseable {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public AbstractMongoRepository(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;
    }

    protected MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    /*--------------------------------------------Additional----------------------------------------------------*/

    protected MongoCollection<? extends Entity> getCollection() {
        return null;
    }

    public String getCollectionName() {
        return null;
    }

    /*----------------------------------------------CRUD-------------------------------------------------------*/

    public abstract T create(T initObj);

    public abstract List<T> read(Bson filter);

    public List<T> readAll() {
        return this.read(Filters.empty());
    }

    public T readByUUID(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter);
        return !list.isEmpty() ? list.get(0) : null;
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

    public abstract boolean updateByReplace(UUID uuid, T dto);

    public boolean delete(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var deletedObj = this.getCollection().findOneAndDelete(filter);
        return deletedObj != null;
    }
}
