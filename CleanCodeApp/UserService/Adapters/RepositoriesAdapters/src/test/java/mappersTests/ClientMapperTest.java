package mappersTests;

import org.junit.jupiter.api.Test;
import tks.gv.userservice.data.entities.ClientEntity;
import tks.gv.userservice.data.mappers.entities.ClientMapper;
import tks.gv.userservice.Client;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientMapperTest {
    UUID uuid = UUID.randomUUID();
    String testFirstName = "John";
    String testLastName = "Smith";
    String testLogin = "12345678";
    String testPassword = "12345678";


    @Test
    void testCreatingMapper() {
        ClientEntity clientDTO1 = new ClientEntity(uuid.toString(), testFirstName, testLastName, testLogin, testPassword,
                false);
        assertNotNull(clientDTO1);

        assertEquals(uuid, UUID.fromString(clientDTO1.getId()));
        assertEquals(testFirstName, clientDTO1.getFirstName());
        assertEquals(testLastName, clientDTO1.getLastName());
        assertEquals(testLogin, clientDTO1.getLogin());
        assertFalse(clientDTO1.isArchive());
    }

    @Test
    void testToMongoClientMethod() {
        Client client = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword);
        assertNotNull(client);

        ClientEntity clientDTO = ClientMapper.toUserEntity(client);
        assertNotNull(clientDTO);

        assertEquals(client.getId(), UUID.fromString(clientDTO.getId()));
        assertEquals(client.getFirstName(), clientDTO.getFirstName());
        assertEquals(client.getLastName(), clientDTO.getLastName());
        assertEquals(client.getLogin(), clientDTO.getLogin());
        assertFalse(clientDTO.isArchive());
    }

    @Test
    void testFromMongoClientMethod() {
        ClientEntity clientDTO1 = new ClientEntity(uuid.toString(), testFirstName, testLastName, testLogin, testPassword,
                true);
        assertNotNull(clientDTO1);

        Client client1 = ClientMapper.fromUserEntity(clientDTO1);
        assertNotNull(client1);

        assertEquals(UUID.fromString(clientDTO1.getId()), client1.getId());
        assertEquals(clientDTO1.getFirstName(), client1.getFirstName());
        assertEquals(clientDTO1.getLastName(), client1.getLastName());
        assertEquals(clientDTO1.getLogin(), client1.getLogin());
        assertTrue(client1.isArchive());
    }
}
