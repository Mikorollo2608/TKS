package adaptersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.aggregates.ClientMongoRepositoryAdapter;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.RepositoryAdapterException;
import tks.gv.exceptions.ClientLoginException;
import tks.gv.repositories.ClientMongoRepository;
import tks.gv.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class ClientAdapterRepositoryTest {
    @Mock
    ClientMongoRepository repository;
    @InjectMocks
    ClientMongoRepositoryAdapter adapter;

    Client testClient;
    Client testClient2;
    Client testClient3;
    ClientEntity testClientEntity;
    ClientEntity testClientEntity2;
    ClientEntity testClientEntity3;

    @BeforeEach
    void init() {
        testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testLoginKlient", "Haslo1234!", "normal");
        testClient2 = new Client(UUID.randomUUID(), "Krzysztof", "Scala", "testLoginClient2", "Haslo1234!", "normal");
        testClient3 = new Client(UUID.randomUUID(), "Jan", "Kann", "testClient3", "Haslo1234!", "normal");

        testClientEntity = ClientMapper.toEntity(testClient);
        testClientEntity2 = ClientMapper.toEntity(testClient2);
        testClientEntity3 = ClientMapper.toEntity(testClient3);
    }

    @Test
    void testConstructingAdapter() {
        ClientMongoRepositoryAdapter testAdapt = new ClientMongoRepositoryAdapter(repository);
        assertNotNull(testAdapt);
    }

    @Test
    void testAddClient() {
        Mockito.when(repository.create(eq(ClientMapper.toEntity(testClient)))).thenReturn(ClientMapper.toEntity(testClient));
        adapter.addClient(testClient);
        Mockito.verify(repository, Mockito.times(1)).create(ClientMapper.toEntity(testClient));
    }

    @Test
    void testAddNeg() {
        Mockito.when(repository.create(Mockito.any())).thenThrow(new MyMongoException("Test exception"));
        assertThrows(RepositoryAdapterException.class, () -> adapter.addClient(testClient2));
    }

    @Test
    void testGetAllClients() {
        List<ClientEntity> clients = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        assertEquals(0, clients.size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, clients.size());

        List<Client> clientList = adapter.getAllClients();
        assertEquals(3, clientList.size());
        assertEquals(testClient, clientList.get(0));
        assertEquals(testClient2, clientList.get(1));
        assertEquals(testClient3, clientList.get(2));
    }

    @Test
    void testGetClientById() {
        List<ClientEntity> clients = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity);

        assertEquals(0, repository.readAll().size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals(testClient, adapter.getClientById(testClient.getId()));
    }

    @Test
    void testGetClientByLogin() {
        List<ClientEntity> clients = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testClientEntity2));

        assertEquals(0, repository.readAll().size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals(testClient2, adapter.getClientByLogin(testClient2.getLogin()));
    }

    @Test
    void testGetClientByLoginMatching() {
        List<ClientEntity> clients = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testClientEntity, testClientEntity2));

        assertEquals(0, repository.readAll().size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, repository.readAll().size());

        List<Client> clientMatchingList = adapter.getClientByLoginMatching("testLogin");
        assertEquals(2, clientMatchingList.size());
        assertEquals(testClient, clientMatchingList.get(0));
        assertEquals(testClient2, clientMatchingList.get(1));
    }

    @Test
    void testModifyClient() {
        List<ClientEntity> clients = new ArrayList<>();

        Client modifiedClient = new Client(
                testClient.getId(),
                "Artur",
                testClient.getLastName(),
                testClient.getLogin(),
                testClient.getPassword(),
                testClient.getClientTypeName()
        );

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of());
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toEntity(modifiedClient));
        Mockito.when(repository.updateByReplace(Mockito.any(),Mockito.any())).thenReturn(true);

        assertEquals(0, repository.readAll().size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals("Adam", ((Client) adapter.getClientById(testClient.getId())).getFirstName());
        adapter.modifyClient(modifiedClient);
        assertEquals("Artur", ((Client) adapter.getClientById(testClient.getId())).getFirstName());
    }

    @Test
    void testModifyClientLoginToOccupiedLogin() {
        List<ClientEntity> clients = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testClientEntity));
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity);

        Client modifiedClient = new Client(
                testClient.getId(),
                testClient.getFirstName(),
                testClient.getLastName(),
                "testLoginClient2",
                testClient.getPassword(),
                testClient.getClientTypeName()
        );

        assertEquals(0, repository.readAll().size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals("testLoginKlient", ((Client) adapter.getClientById(testClient.getId())).getLogin());
        assertThrows(ClientLoginException.class, () -> adapter.modifyClient(modifiedClient));
        assertEquals("testLoginKlient", ((Client) adapter.getClientById(testClient.getId())).getLogin());

    }

    @Test
    void testActivateClient() {
        List<ClientEntity> clients = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        testClient.setArchive(true);
        testClientEntity = ClientMapper.toEntity(testClient);
        testClient.setArchive(false);
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toEntity(testClient));

        assertEquals(0, repository.readAll().size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, repository.readAll().size());

        assertTrue(adapter.getClientById(testClient.getId()).isArchive());
        adapter.activateClient(testClient.getId());
        assertFalse(adapter.getClientById(testClient.getId()).isArchive());
    }

    @Test
    void testDeactivateClient() {
        List<ClientEntity> clients = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {clients.add(testClientEntity); return testClientEntity;})
                .then(i -> {clients.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {clients.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(clients);

        testClient.setArchive(false);
        testClientEntity = ClientMapper.toEntity(testClient);
        testClient.setArchive(true);
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toEntity(testClient));

        assertEquals(0, repository.readAll().size());
        adapter.addClient(testClient);
        adapter.addClient(testClient2);
        adapter.addClient(testClient3);
        assertEquals(3, repository.readAll().size());

        assertFalse(adapter.getClientById(testClient.getId()).isArchive());
        adapter.activateClient(testClient.getId());
        assertTrue(adapter.getClientById(testClient.getId()).isArchive());
    }

}