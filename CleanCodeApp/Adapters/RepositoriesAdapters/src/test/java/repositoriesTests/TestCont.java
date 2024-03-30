package repositoriesTests;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

public class TestCont {

    @Test
    void testCon() {
        GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"));

        List<String> list = new ArrayList<>();
        list.add("MONGO_INITDB_ROOT_USERNAME=admin");
        list.add("MONGO_INITDB_ROOT_PASSWORD=adminpassword");
        list.add("MONGO_INITDB_DATABASE=admin");

        mongoDBContainer.setEnv(list);
        mongoDBContainer.start();

    }
}
