package tks.gv.repositories.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DBConfig {

    private final ConnectionString connectionString = new ConnectionString(
            // Local with docker
            "mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=replica_set_single"
            // MongoDB Atlas (cloud)
//            "mongodb+srv://Michal:ZvDI3RNUGeTKjHTU@atlascluster.pweqkng.mongodb.net/"
    );
    private final MongoCredential credential = MongoCredential.createCredential("admin", "admin",
            "adminpassword".toCharArray());

    private final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
            .automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
            .build());

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase("reserveACourt");
    }

}
