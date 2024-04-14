//package repositoriesTests;
//
//import com.github.dockerjava.api.model.ExposedPort;
//import com.github.dockerjava.api.model.PortBinding;
//import com.github.dockerjava.api.model.Ports;
//import org.junit.jupiter.api.Test;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.utility.DockerImageName;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TestCont {
//
//    @Test
//    void testCon() throws InterruptedException, IOException {
//        Map<String, String> map = new HashMap<>(
//                Map.of(
//                        "MONGO_INITDB_ROOT_USERNAME", "admin",
//                        "MONGO_INITDB_ROOT_PASSWORD", "adminpassword"
//                )
//        );
//
//        GenericContainer<?> mongoDBContainer_1 = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
//                .withEnv(map)
//                .withCreateContainerCmdModifier(createContainerCmd -> {
//                    createContainerCmd.withName("mongodb1");
//                    createContainerCmd.withHostName("mongodb1");
//                    createContainerCmd.withEnv(List.of("MONGO_INITDB_ROOT_USERNAME=admin", "MONGO_INITDB_ROOT_PASSWORD=adminpassword"));
//                    createContainerCmd.withEntrypoint("/bin/bash", "-c",
//                            "openssl rand -base64 756 > docker-entrypoint-initdb.d/keyFile && chmod 400 docker-entrypoint-initdb.d/keyFile && " +
//                                    "mkdir /etc/mongo && mv /docker-entrypoint-initdb.d/keyFile /etc/mongo &&" +
//                                    "echo \"replication:\n" +
//                                    "    replSetName: rs0\n" +
//                                    "security:\n" +
//                                    "    keyFile: /etc/mongo/keyFile\"" +
//                                    "> /etc/mongod.conf"
//                                    + " && mongod --config /etc/mongod.conf --auth --bind_ip localhost,mongodb1");
//                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(27017), new ExposedPort(27017)));
//                })
//                .withExposedPorts(27017);
//        mongoDBContainer_1.start();
//
//
//        mongoDBContainer_1.execInContainer("/bin/bash", "-c", "mongosh --host localhost:27017 --authenticationDatabase admin --eval 'rs.initiate()'");
//        mongoDBContainer_1.execInContainer("/bin/bash", "-c", "printf \" use admin\ndb.createUser({user: \\\"admin\\\", pwd: \\\"adminpassword\\\", roles: [{ role: \\\"root\\\", db: \\\"admin\\\" }] })\n\" > /tmp/test.js");
//        mongoDBContainer_1.execInContainer("/bin/bash", "-c", "mongosh --host localhost:27017 < /tmp/test.js");
//
//
//        String connectionString = "mongodb://%s:%s".formatted(mongoDBContainer_1.getHost(), mongoDBContainer_1.getFirstMappedPort());
//
//        System.out.println(connectionString);
//
//        Thread.sleep(1000000);
//    }
//}
//
//
////package repositoriesTests;
////
////import com.github.dockerjava.api.model.ExposedPort;
////import com.github.dockerjava.api.model.PortBinding;
////import com.github.dockerjava.api.model.Ports;
////import org.junit.jupiter.api.Test;
////import org.testcontainers.containers.GenericContainer;
////import org.testcontainers.utility.DockerImageName;
////import tks.gv.repositories.config.DBConfig;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class TestCont {
////
////    @Test
////    void testCon() throws InterruptedException {
////        List<String> list = new ArrayList<>(
////                List.of(
////                        "MONGO_INITDB_ROOT_USERNAME=admin",
////                        "MONGO_INITDB_ROOT_PASSWORD=adminpassword",
////                        "MONGO_INITDB_DATABASE=admin"
////                )
////        );
////        GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"));
////
////        mongoDBContainer.withCreateContainerCmdModifier(createContainerCmd -> {
////            createContainerCmd.withName("mongodb1");
////            createContainerCmd.withHostName("mongodb1");
////            createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(60000), new ExposedPort(27017)));
////        });
////        mongoDBContainer.addExposedPort(27017);
////        mongoDBContainer.setEnv(list);
////
////        mongoDBContainer.start();
////
////        String connectionString = "mongodb://%s:%s".formatted(mongoDBContainer.getHost(), mongoDBContainer.getFirstMappedPort());
////
////        System.out.println(connectionString);
////        System.out.println(mongoDBContainer.getContainerName());
////
////        Thread.sleep(1000000);
////
////        DBConfig dbconfig = new DBConfig(connectionString, "admin", "adminpassword", "admin");
////
////        System.out.println(dbconfig.mongoDatabase(dbconfig.mongoClient()).getName());
////    }
////
////}