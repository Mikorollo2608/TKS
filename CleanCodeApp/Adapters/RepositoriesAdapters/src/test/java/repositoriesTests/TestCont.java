package repositoriesTests;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCont {

    static String mongoDB_1 = "";

    @Test
    void testCon() throws InterruptedException, IOException {
        Network network = Network.newNetwork();

        Map<String, String> map = new HashMap<>(
                Map.of(
                        "MONGO_INITDB_ROOT_USERNAME", "admin",
                        "MONGO_INITDB_ROOT_PASSWORD", "adminpassword"
                )
        );

        GenericContainer<?> mongoDBContainer_1 = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
                .withEnv(map)
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withName("mongodb1");
                    createContainerCmd.withHostName("mongodb1");
                    createContainerCmd.withEnv(List.of("MONGO_INITDB_ROOT_USERNAME=admin", "MONGO_INITDB_ROOT_PASSWORD=adminpassword"));
                    createContainerCmd.withEntrypoint("/bin/bash", "-c",
                            "openssl rand -base64 756 > docker-entrypoint-initdb.d/keyFile && chmod 400 docker-entrypoint-initdb.d/keyFile && " +
                                    "mkdir /etc/mongo && mv /docker-entrypoint-initdb.d/keyFile /etc/mongo &&" +
                                    "echo \"replication:\n" +
                                    "    replSetName: rs0\n" +
                                    "security:\n" +
                                    "    keyFile: /etc/mongo/keyFile\"" +
                                    "> /etc/mongod.conf"
                                    + " && mongod --config /etc/mongod.conf --replSet rs0 --auth");
                })
                .withExposedPorts(27017)
                .withNetwork(network)
                .withNetworkAliases("mongodb1");
        mongoDBContainer_1.start();


        mongoDBContainer_1.execInContainer("/bin/bash", "-c",  "mongosh --host localhost:27017 --authenticationDatabase admin --eval 'rs.initiate()'");
//                "mongosh --host localhost:27017 --authenticationDatabase admin --eval 'db.createUser({user: \"admin\", pwd: \"adminpassword\", roles: [{ role: \"root\", db: \"admin\" }] }] })'");

        String connectionString = "mongodb://%s:%s".formatted(mongoDBContainer_1.getHost(), mongoDBContainer_1.getFirstMappedPort());

        System.out.println(connectionString);

        Thread.sleep(1000000);
    }
}
