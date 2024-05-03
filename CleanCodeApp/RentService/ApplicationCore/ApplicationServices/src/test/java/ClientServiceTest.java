import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.exceptions.ClientException;
import tks.gv.exceptions.ClientLoginException;
import tks.gv.exceptions.ClientReadServiceException;
import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.ChangeUserStatusPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;
import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.infrastructure.users.ports.GetUserByLoginPort;
import tks.gv.infrastructure.users.ports.ModifyUserPort;
import tks.gv.Client;
import tks.gv.ClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    AddUserPort addUserPort;
    @Mock
    GetAllUsersPort getAllUsersPort;
    @Mock
    GetUserByIdPort getUserByIdPort;
    @Mock
    GetUserByLoginPort getUserByLoginPort;
    @Mock
    ModifyUserPort modifyUserPort;
    @Mock
    ChangeUserStatusPort changeUserStatusPort;
    @InjectMocks
    final ClientService cm = new ClientService();

    Client testClient;
    Client testClient2;
    Client testClient3;
    final String testClientType = "normal";
    final String testClientPass = "Haslo1234!";

    @BeforeEach
    void init() {
        testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testKlient", testClientPass, testClientType);
        testClient2 = new Client(UUID.randomUUID(), "Tobiasz", "Niezgodka", "testLoginKlient2", testClientPass, testClientType);
        testClient3 = new Client(UUID.randomUUID(), "Adam", "Kociol", "testLoginKlient3", testClientPass, testClientType);
    }

    @Test
    void testCreatingClientManagerNoArgs() {
        ClientService clientService = new ClientService();
        assertNotNull(clientService);
    }

    @Test
    void testCreatingClientManagerAllArgs() {
        ClientService clientService = new ClientService(addUserPort, getAllUsersPort, getUserByIdPort,
                getUserByLoginPort, modifyUserPort, changeUserStatusPort);
        assertNotNull(clientService);
    }

    @Test
    void testGetAllClientsOnlyClients() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testClient, testClient2, testClient3));

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 3);
        assertEquals(testClient, clientList.get(0));
        assertEquals(testClient2, clientList.get(1));
        assertEquals(testClient3, clientList.get(2));
    }

    @Test
    void testGetAllClientsDiffUsers() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testClient));

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 1);
        assertEquals(testClient, clientList.get(0));
    }


    @Test
    void testGetClientById() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testClient, testClient2, testClient3));
        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 3);

        assertEquals(testClient, cm.getClientById(clientList.get(0).getId()));
    }

    @Test
    void testGetClientByIdNull() {
        Mockito.when(getUserByIdPort.getUserById(any())).thenReturn(null);

        assertNull(cm.getClientById(UUID.randomUUID()));
    }

    @Test
    void testGetClientByIdString() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testClient, testClient2, testClient3));
        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 3);

        assertEquals(testClient, cm.getClientById(clientList.get(0).getId().toString()));
    }

    @Test
    void testRegisterNewClient() {
        Mockito.when(addUserPort.addUser(testClient)).thenReturn(testClient);

        assertEquals(testClient, cm.registerClient(testClient));
    }

    @Test
    void testRegisterClientNull() {
        Mockito.when(addUserPort.addUser(testClient)).thenReturn(null);

        assertNull(cm.registerClient(testClient));
    }
    
    @Test
    void testRegisterNewClientNeg() {
        Mockito.when(addUserPort.addUser(testClient)).thenThrow(ClientLoginException.class);

        assertThrows(ClientLoginException.class, () -> cm.registerClient(testClient));
    }

    @Test
    void testGetClientByLogin() {
        Mockito.when(getUserByLoginPort.getUserByLogin("testLoginKlient2")).thenReturn(testClient2);

        assertEquals(testClient2, cm.getClientByLogin("testLoginKlient2"));
    }

    @Test
    void testGetClientByLoginNull() {
        Mockito.when(getUserByLoginPort.getUserByLogin(anyString())).thenReturn(null);

        assertNull(cm.getClientByLogin("testClient"));
    }

    @Test
    void testGetClientByLoginMatching() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching("testLogin")).thenReturn(List.of(testClient2, testClient3));

        List<Client> clientList = cm.getClientByLoginMatching("testLogin");
        assertEquals(clientList.size(), 2);

        assertEquals(testClient2, clientList.get(0));
        assertEquals(testClient3, clientList.get(1));
    }

    @Test
    void testGetClientByLoginMatchingDiffUsers() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching("testLogin")).thenReturn(List.of(testClient2));

        List<Client> clientList = cm.getClientByLoginMatching("testLogin");
        assertEquals(clientList.size(), 1);

        assertEquals(testClient2, clientList.get(0));
    }


    @Test
    void testGetClientByLoginMatchingEmptyList() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching(anyString())).thenReturn(new ArrayList<>());

        assertEquals(0, cm.getClientByLoginMatching("testA").size());
    }

    @Test
    void testModifyClient() {
        Mockito.doNothing().when(modifyUserPort).modifyUser(any(Client.class));

        cm.modifyClient(testClient);
        Mockito.verify(modifyUserPort, Mockito.times(1)).modifyUser(testClient);
    }

    @Test
    void testActivateClient() {
        Mockito.doNothing().when(changeUserStatusPort).activateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateClient(id);
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).activateUser(id);
    }

    @Test
    void testActivateClientStringId() {
        Mockito.doNothing().when(changeUserStatusPort).activateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateClient(id.toString());
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).activateUser(id);
    }

    @Test
    void testDeactivateClient() {
        Mockito.doNothing().when(changeUserStatusPort).deactivateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateClient(id);
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).deactivateUser(id);
    }

    @Test
    void testDeactivateClientStringId() {
        Mockito.doNothing().when(changeUserStatusPort).deactivateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateClient(id.toString());
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).deactivateUser(id);
    }
}
