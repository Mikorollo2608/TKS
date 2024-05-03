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
import tks.gv.repositories.config.DBConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupTestContainer {

    static final String testDBName = "testmongodb_rent1";

    static DBConfig dbconfig;
    static MongoClient mongoClient;
    static MongoDatabase mongoDatabase;

    private static GenericContainer<?> mongoDBContainer;

    @BeforeAll
    static void init () throws IOException, InterruptedException {
        Map<String, String> map = new HashMap<>(
                Map.of(
                        "MONGO_INITDB_ROOT_USERNAME", "admin",
                        "MONGO_INITDB_ROOT_PASSWORD", "adminpassword"
                )
        );

        mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
                .withEnv(map)
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withName(testDBName);
                    createContainerCmd.withHostName(testDBName);
                    createContainerCmd.withEntrypoint("/bin/bash", "-c",
                            "openssl rand -base64 756 > docker-entrypoint-initdb.d/keyFile && chmod 400 docker-entrypoint-initdb.d/keyFile && " +
                                    "mkdir /etc/mongo && mv /docker-entrypoint-initdb.d/keyFile /etc/mongo &&" +
                                    "echo \"replication:\n" +
                                    "    replSetName: rs0\n" +
                                    "security:\n" +
                                    "    keyFile: /etc/mongo/keyFile\"" +
                                    "> /etc/mongod.conf"
                                    + " && mongod --config /etc/mongod.conf --auth --bind_ip localhost,%s".formatted(testDBName));
                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(27017), new ExposedPort(27017)));
                })
                .withExposedPorts(27017);
        mongoDBContainer.start();


        mongoDBContainer.execInContainer("/bin/bash", "-c", "mongosh --host localhost:27017 --authenticationDatabase admin --eval 'rs.initiate()'");
        mongoDBContainer.execInContainer("/bin/bash", "-c", "printf \" use admin\ndb.createUser({user: \\\"admin\\\", pwd: \\\"adminpassword\\\", roles: [{ role: \\\"root\\\", db: \\\"admin\\\" }] })\n\" > /tmp/test.js");
        mongoDBContainer.execInContainer("/bin/bash", "-c", "mongosh --host localhost:27017 < /tmp/test.js");


        String connectionString = "mongodb://%s:%s/?replicaSet=rs0".formatted(mongoDBContainer.getHost(), mongoDBContainer.getFirstMappedPort());

        dbconfig = new DBConfig(connectionString, "admin", "adminpassword", "admin");
        mongoClient = dbconfig.mongoClient();
        mongoDatabase = dbconfig.mongoDatabase(mongoClient);
    }

    @AfterAll
    static void destroy() {
        mongoDBContainer.close();
    }
}
