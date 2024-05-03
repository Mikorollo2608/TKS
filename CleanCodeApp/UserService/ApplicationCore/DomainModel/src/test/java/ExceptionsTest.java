import org.junit.jupiter.api.Test;
import tks.gv.userservice.exceptions.MainException;
import tks.gv.userservice.exceptions.MyMongoException;
import tks.gv.userservice.exceptions.RepositoryException;
import tks.gv.userservice.exceptions.UserException;
import tks.gv.userservice.exceptions.RepositoryAdapterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionsTest {

    @Test
    void testMainException() {
        RuntimeException mainException = new MainException("TEST");
        assertThrows(MainException.class, () -> {throw mainException;});
        assertEquals("TEST", mainException.getMessage());
    }

    @Test
    void testClientException() {
        RuntimeException clientException = new UserException("TEST");
        assertThrows(UserException.class, () -> {throw clientException;});
        assertEquals("TEST", clientException.getMessage());
    }

    @Test
    void testRepositoryException() {
        RuntimeException repositoryException = new RepositoryException("TEST");
        assertThrows(RepositoryException.class, () -> {throw repositoryException;});
        assertEquals("TEST", repositoryException.getMessage());
    }

    @Test
    void testRepositoryAdapterException() {
        RuntimeException jakartaException = new RepositoryAdapterException("TEST");
        assertThrows(RepositoryAdapterException.class, () -> {throw jakartaException;});
        assertEquals("TEST", jakartaException.getMessage());
    }

    @Test
    void testMyMongoException() {
        RuntimeException myMongoException = new MyMongoException("TEST");
        assertThrows(MyMongoException.class, () -> {throw myMongoException;});
        assertEquals("TEST", myMongoException.getMessage());
    }
}
