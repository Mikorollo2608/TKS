package tks.gv.rentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tks.gv.rentservice.Client;

import java.util.UUID;

public class ClientTest {
    String testLogin = "12345678";
    String testTypeAthlete = "athlete";
    String testTypeCoach = "coach";
    String testTypeNormal = "normal";

    @Test
    void testCreatingClient() {
        Client client = new Client(UUID.randomUUID(), testLogin, testTypeNormal);
        assertNotNull(client);

        assertEquals(testLogin, client.getLogin());
        assertEquals(testTypeNormal, client.getClientTypeName());
        assertFalse(client.isArchive());
    }

    @Test
    void testSetters() {
        Client client = new Client(UUID.randomUUID(), testLogin, testTypeNormal);
        assertNotNull(client);

        assertFalse(client.isArchive());
        client.setArchive(true);
        assertTrue(client.isArchive());
        client.setArchive(false);
        assertFalse(client.isArchive());

        assertEquals(testTypeNormal, client.getClientTypeName());
        client.setClientTypeName(testTypeAthlete);
        assertEquals(testTypeAthlete, client.getClientTypeName());
        client.setClientTypeName(testTypeCoach);
        assertEquals(testTypeCoach, client.getClientTypeName());
    }

    @Test
    void testGettingClientMaxHoursAndApplyingDiscount() {
        Client client = new Client(UUID.randomUUID(), testLogin, testTypeNormal);
        assertNotNull(client);
        Client client1 = new Client(UUID.randomUUID(), testLogin, testTypeAthlete);
        assertNotNull(client1);
        Client client2 = new Client(UUID.randomUUID(), testLogin, testTypeCoach);
        assertNotNull(client2);

        assertEquals(0, client.applyDiscount());
        assertEquals(0.1, client1.applyDiscount());
        assertEquals(0.2, client2.applyDiscount());

        assertEquals(3, client.clientMaxHours());
        assertEquals(6, client1.clientMaxHours());
        assertEquals(12, client2.clientMaxHours());
    }
}
