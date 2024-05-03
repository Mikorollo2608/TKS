import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tks.gv.userservice.Client;

import java.util.UUID;

public class ClientTest {
    String testFirstName = "John";
    String testLastName = "Smith";
    String testLogin = "12345678";
    String testPassword = "12345678";
    @Test
    void testCreatingClient() {
        Client client = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword);
        assertNotNull(client);

        assertEquals(testFirstName, client.getFirstName());
        assertEquals(testLastName, client.getLastName());
        assertEquals(testLogin, client.getLogin());
        assertFalse(client.isArchive());
    }

    @Test
    void testSetters() {
        Client client = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword);
        assertNotNull(client);

        assertEquals(testFirstName, client.getFirstName());
        client.setFirstName("Adam");
        assertEquals("Adam", client.getFirstName());

        assertEquals(testLastName, client.getLastName());
        client.setLastName("Long");
        assertEquals("Long", client.getLastName());

        assertFalse(client.isArchive());
        client.setArchive(true);
        assertTrue(client.isArchive());
        client.setArchive(false);
        assertFalse(client.isArchive());
    }
}
