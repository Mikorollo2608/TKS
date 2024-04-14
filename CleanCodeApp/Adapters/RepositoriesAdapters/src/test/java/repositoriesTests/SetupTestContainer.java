package repositoriesTests;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import tks.gv.repositories.CourtMongoRepository;
import tks.gv.repositories.config.DBConfig;

import java.util.HashMap;
import java.util.Map;

public class SetupTestContainer {

    static final String testDBName = "testmongodb1";

    static DBConfig dbconfig;
    static MongoClient mongoClient;
    static MongoDatabase mongoDatabase;

    private static GenericContainer<?> mongoDBContainer;

    @BeforeAll
    static void init () {
        Map<String, String> map = new HashMap<>(
                Map.of(
                        "MONGO_INITDB_ROOT_USERNAME", "admin",
                        "MONGO_INITDB_ROOT_PASSWORD", "adminpassword"
                )
        );
        mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withName(testDBName);
                    createContainerCmd.withHostName(testDBName);
                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(60000), new ExposedPort(27017)));
                })
                .withExposedPorts(27017)
                .withEnv(map);

        mongoDBContainer.start();

        String connectionString = "mongodb://%s:%s".formatted(mongoDBContainer.getHost(), mongoDBContainer.getFirstMappedPort());

        dbconfig = new DBConfig(connectionString, "admin", "adminpassword", "admin");
        mongoClient = dbconfig.mongoClient();
        mongoDatabase = dbconfig.mongoDatabase(mongoClient);
    }

    @AfterAll
    static void destroy() {
        mongoDBContainer.close();
    }
}
