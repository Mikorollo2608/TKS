package tks.gv.rentservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.rentservice.exceptions.ClientLoginException;
import tks.gv.rentservice.infrastructure.clients.ports.AddClientPort;
import tks.gv.rentservice.infrastructure.clients.ports.ChangeClientStatusPort;
import tks.gv.rentservice.infrastructure.clients.ports.GetAllClientsPort;
import tks.gv.rentservice.infrastructure.clients.ports.GetClientByIdPort;
import tks.gv.rentservice.infrastructure.clients.ports.GetClientByLoginPort;
import tks.gv.rentservice.infrastructure.clients.ports.ModifyClientPort;
import tks.gv.rentservice.Client;
import tks.gv.rentservice.ClientService;

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
    AddClientPort addClientPort;
    @Mock
    GetAllClientsPort getAllClientsPort;
    @Mock
    GetClientByIdPort getClientByIdPort;
    @Mock
    GetClientByLoginPort getClientByLoginPort;
    @Mock
    ModifyClientPort modifyClientPort;
    @Mock
    ChangeClientStatusPort changeClientStatusPort;
    @InjectMocks
    final ClientService cm = new ClientService();

    Client testClient;
    Client testClient2;
    Client testClient3;
    final String testClientType = "normal";
    final String testClientPass = "Haslo1234!";

    @BeforeEach
    void init() {
        testClient = new Client(UUID.randomUUID(), "testKlient", testClientType);
        testClient2 = new Client(UUID.randomUUID(), "testLoginKlient2", testClientType);
        testClient3 = new Client(UUID.randomUUID(), "testLoginKlient3", testClientType);
    }

    @Test
    void testCreatingClientManagerNoArgs() {
        ClientService clientService = new ClientService();
        assertNotNull(clientService);
    }

    @Test
    void testCreatingClientManagerAllArgs() {
        ClientService clientService = new ClientService(addClientPort, getAllClientsPort, getClientByIdPort,
                getClientByLoginPort, modifyClientPort, changeClientStatusPort);
        assertNotNull(clientService);
    }

    @Test
    void testGetAllClientsOnlyClients() {
        Mockito.when(getAllClientsPort.getAllClients()).thenReturn(List.of(testClient, testClient2, testClient3));

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 3);
        assertEquals(testClient, clientList.get(0));
        assertEquals(testClient2, clientList.get(1));
        assertEquals(testClient3, clientList.get(2));
    }

    @Test
    void testGetAllClientsDiffClients() {
        Mockito.when(getAllClientsPort.getAllClients()).thenReturn(List.of(testClient));

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 1);
        assertEquals(testClient, clientList.get(0));
    }


    @Test
    void testGetClientById() {
        Mockito.when(getAllClientsPort.getAllClients()).thenReturn(List.of(testClient, testClient2, testClient3));
        Mockito.when(getClientByIdPort.getClientById(testClient.getId())).thenReturn(testClient);

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 3);

        assertEquals(testClient, cm.getClientById(clientList.get(0).getId()));
    }

    @Test
    void testGetClientByIdNull() {
        Mockito.when(getClientByIdPort.getClientById(any())).thenReturn(null);

        assertNull(cm.getClientById(UUID.randomUUID()));
    }

    @Test
    void testGetClientByIdString() {
        Mockito.when(getAllClientsPort.getAllClients()).thenReturn(List.of(testClient, testClient2, testClient3));
        Mockito.when(getClientByIdPort.getClientById(testClient.getId())).thenReturn(testClient);

        List<Client> clientList = cm.getAllClients();
        assertEquals(clientList.size(), 3);

        assertEquals(testClient, cm.getClientById(clientList.get(0).getId().toString()));
    }

    @Test
    void testRegisterNewClient() {
        Mockito.when(addClientPort.addClient(testClient)).thenReturn(testClient);

        assertEquals(testClient, cm.registerClient(testClient));
    }

    @Test
    void testRegisterClientNull() {
        Mockito.when(addClientPort.addClient(testClient)).thenReturn(null);

        assertNull(cm.registerClient(testClient));
    }
    
    @Test
    void testRegisterNewClientNeg() {
        Mockito.when(addClientPort.addClient(testClient)).thenThrow(ClientLoginException.class);

        assertThrows(ClientLoginException.class, () -> cm.registerClient(testClient));
    }

    @Test
    void testGetClientByLogin() {
        Mockito.when(getClientByLoginPort.getClientByLogin("testLoginKlient2")).thenReturn(testClient2);

        assertEquals(testClient2, cm.getClientByLogin("testLoginKlient2"));
    }

    @Test
    void testGetClientByLoginNull() {
        Mockito.when(getClientByLoginPort.getClientByLogin(anyString())).thenReturn(null);

        assertNull(cm.getClientByLogin("testClient"));
    }

    @Test
    void testGetClientByLoginMatching() {
        Mockito.when(getClientByLoginPort.getClientByLoginMatching("testLogin")).thenReturn(List.of(testClient2, testClient3));

        List<Client> clientList = cm.getClientByLoginMatching("testLogin");
        assertEquals(clientList.size(), 2);

        assertEquals(testClient2, clientList.get(0));
        assertEquals(testClient3, clientList.get(1));
    }

    @Test
    void testGetClientByLoginMatchingDiffClients() {
        Mockito.when(getClientByLoginPort.getClientByLoginMatching("testLogin")).thenReturn(List.of(testClient2));

        List<Client> clientList = cm.getClientByLoginMatching("testLogin");
        assertEquals(clientList.size(), 1);

        assertEquals(testClient2, clientList.get(0));
    }


    @Test
    void testGetClientByLoginMatchingEmptyList() {
        Mockito.when(getClientByLoginPort.getClientByLoginMatching(anyString())).thenReturn(new ArrayList<>());

        assertEquals(0, cm.getClientByLoginMatching("testA").size());
    }

    @Test
    void testModifyClient() {
        Mockito.doNothing().when(modifyClientPort).modifyClient(any(Client.class));

        cm.modifyClient(testClient);
        Mockito.verify(modifyClientPort, Mockito.times(1)).modifyClient(testClient);
    }

    @Test
    void testActivateClient() {
        Mockito.doNothing().when(changeClientStatusPort).activateClient(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateClient(id);
        Mockito.verify(changeClientStatusPort, Mockito.times(1)).activateClient(id);
    }

    @Test
    void testActivateClientStringId() {
        Mockito.doNothing().when(changeClientStatusPort).activateClient(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateClient(id.toString());
        Mockito.verify(changeClientStatusPort, Mockito.times(1)).activateClient(id);
    }

    @Test
    void testDeactivateClient() {
        Mockito.doNothing().when(changeClientStatusPort).deactivateClient(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateClient(id);
        Mockito.verify(changeClientStatusPort, Mockito.times(1)).deactivateClient(id);
    }

    @Test
    void testDeactivateClientStringId() {
        Mockito.doNothing().when(changeClientStatusPort).deactivateClient(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateClient(id.toString());
        Mockito.verify(changeClientStatusPort, Mockito.times(1)).deactivateClient(id);
    }
}
