package repositoriesTests;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;
import tks.gv.repositories.config.DBConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCont {

    @Test
    void testCon() throws InterruptedException, IOException {
        Network network = Network.newNetwork();

        Map<String, String> map = new HashMap<>(
                Map.of(
                        "MONGO_INITDB_ROOT_USERNAME", "admin",
                        "MONGO_INITDB_ROOT_PASSWORD", "adminpassword",
                        "MONGO_INITDB_DATABASE", "admin"
                )
        );

        GenericContainer<?> mongoDBContainer_1 = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withName("mongodbtest1");
                    createContainerCmd.withHostName("mongodbtest1");
                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(27017), new ExposedPort(27017)));
                })
                .withExposedPorts(27017)
                .withEnv(map)
                .withNetwork(network)
                .withNetworkAliases("mongodbtest1");
//                .withCommand("--replSet rs0 --bind_ip localhost,mongodbtest1");
//        mongoDBContainer_1.setPortBindings(List.of("61627:27017"));
        mongoDBContainer_1.start();

        GenericContainer<?> mongoDBContainer_2 = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withName("mongodbtest2");
                    createContainerCmd.withHostName("mongodbtest2");
                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(27018), new ExposedPort(27018)));
                })
                .withExposedPorts(27018)
                .withEnv(map)
                .withNetwork(network)
                .withNetworkAliases("mongodbtest2");
//                .withCommand("--replSet rs0 --bind_ip localhost,mongodbtest2");
//        mongoDBContainer_1.setPortBindings(List.of("61628:27018"));
        mongoDBContainer_2.start();

        GenericContainer<?> mongoDBContainer_3 = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withName("mongodbtest3");
                    createContainerCmd.withHostName("mongodbtest3");
                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(27019), new ExposedPort(27019)));
                })
                .withExposedPorts(27019)
                .withEnv(map)
                .withNetwork(network)
                .withNetworkAliases("mongodbtest3");
//                .withCommand("--replSet rs0 --bind_ip localhost,mongodbtest3");
//        mongoDBContainer_1.setPortBindings(List.of("61629:27019"));
        mongoDBContainer_3.start();
//
//        mongoDBContainer_1.execInContainer("/bin/bash", "-c", "mongo --eval 'printjson(rs.initiate({_id:\"rs0\"," +
//                "members:[{_id:0,host:\"mongodbtest1:27017\"},{_id:1,host:\"mongodbtest2:27017\"},{_id:2,host:\"mongodbtest3:27017\"}]}))' --quiet");

//        String connectionString = "mongodb://%s:%s".formatted(mongoDBContainer_1.getHost(), mongoDBContainer_1.getFirstMappedPort());
//
//        System.out.println(connectionString);
//        System.out.println(mongoDBContainer_1.getContainerName());

        Thread.sleep(1000000);

//        DBConfig dbconfig = new DBConfig(connectionString, "admin", "adminpassword", "admin");
//
//        System.out.println(dbconfig.mongoDatabase(dbconfig.mongoClient()).getName());
    }

}
