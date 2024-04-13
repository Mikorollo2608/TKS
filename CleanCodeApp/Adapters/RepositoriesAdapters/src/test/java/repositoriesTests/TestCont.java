package repositoriesTests;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import tks.gv.repositories.config.DBConfig;

import java.util.ArrayList;
import java.util.List;

public class TestCont {

    @Test
    void testCon() throws InterruptedException {
        List<String> list = new ArrayList<>(
                List.of(
                        "MONGO_INITDB_ROOT_USERNAME=admin",
                        "MONGO_INITDB_ROOT_PASSWORD=adminpassword",
                        "MONGO_INITDB_DATABASE=admin"
                )
        );
        GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"));

        mongoDBContainer.withCreateContainerCmdModifier(createContainerCmd -> {
            createContainerCmd.withName("mongodb1");
            createContainerCmd.withHostName("mongodb1");
        });
        mongoDBContainer.addExposedPort(27017);
        mongoDBContainer.setEnv(list);

        mongoDBContainer.start();

        String connectionString = "mongodb://%s:%s".formatted(mongoDBContainer.getHost(), mongoDBContainer.getFirstMappedPort());

        System.out.println(connectionString);
        System.out.println(mongoDBContainer.getContainerName());

//        Thread.sleep(1000000);

        DBConfig dbconfig = new DBConfig(connectionString, "admin", "adminpassword", "admin");

        System.out.println(dbconfig.mongoDatabase(dbconfig.mongoClient()).getName());
    }

}
