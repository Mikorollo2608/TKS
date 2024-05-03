package adaptersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.aggregates.UserMongoRepositoryAdapter;
import tks.gv.data.entities.UserEntity;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.RepositoryAdapterException;
import tks.gv.exceptions.UserLoginException;
import tks.gv.repositories.UserMongoRepository;
import tks.gv.users.Client;
import tks.gv.users.User;

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
public class UserAdapterRepositoryTest {
    @Mock
    UserMongoRepository repository;
    @InjectMocks
    UserMongoRepositoryAdapter adapter;

    Client testClient;
    Client testClient2;
    Client testClient3;
    UserEntity testClientEntity;
    UserEntity testClientEntity2;
    UserEntity testClientEntity3;

    @BeforeEach
    void init() {
        testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testLoginKlient", "Haslo1234!", "normal");
        testClient2 = new Client(UUID.randomUUID(), "Krzysztof", "Scala", "testLoginClient2", "Haslo1234!", "normal");
        testClient3 = new Client(UUID.randomUUID(), "Jan", "Kann", "testClient3", "Haslo1234!", "normal");

        testClientEntity = ClientMapper.toUserEntity(testClient);
        testClientEntity2 = ClientMapper.toUserEntity(testClient2);
        testClientEntity3 = ClientMapper.toUserEntity(testClient3);
    }

    @Test
    void testConstructingAdapter() {
        UserMongoRepositoryAdapter testAdapt = new UserMongoRepositoryAdapter(repository);
        assertNotNull(testAdapt);
    }

    @Test
    void testAddClient() {
        Mockito.when(repository.create(eq(ClientMapper.toUserEntity(testClient)))).thenReturn(ClientMapper.toUserEntity(testClient));
        adapter.addUser(testClient);
        Mockito.verify(repository, Mockito.times(1)).create(ClientMapper.toUserEntity(testClient));
    }

    @Test
    void testAddNeg() {
        Mockito.when(repository.create(Mockito.any())).thenThrow(new MyMongoException("Test exception"));
        assertThrows(RepositoryAdapterException.class, () -> adapter.addUser(testClient2));
    }

    @Test
    void testGetAllUsers() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

        assertEquals(0, users.size());
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, users.size());

        List<User> userList = adapter.getAllUsers();
        assertEquals(3, userList.size());
        assertEquals(testClient, userList.get(0));
        assertEquals(testClient2, userList.get(1));
        assertEquals(testClient3, userList.get(2));
    }

    @Test
    void testGetUserById() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity);

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals(testClient, adapter.getUserById(testClient.getId()));
    }

    @Test
    void testGetUserByLogin() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testClientEntity2));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals(testClient2, adapter.getUserByLogin(testClient2.getLogin()));
    }

    @Test
    void testGetUserByLoginMatching() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testClientEntity, testClientEntity2));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, repository.readAll().size());

        List<User> userMatchingList = adapter.getUserByLoginMatching("testLogin");
        assertEquals(2, userMatchingList.size());
        assertEquals(testClient, userMatchingList.get(0));
        assertEquals(testClient2, userMatchingList.get(1));
    }

    @Test
    void testModifyUser() {
        List<UserEntity> users = new ArrayList<>();

        Client modifiedClient = new Client(
                testClient.getId(),
                "Artur",
                testClient.getLastName(),
                testClient.getLogin(),
                testClient.getPassword(),
                testClient.getClientTypeName()
        );

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of());
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toUserEntity(modifiedClient));
        Mockito.when(repository.updateByReplace(Mockito.any(),Mockito.any())).thenReturn(true);

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals("Adam", ((Client) adapter.getUserById(testClient.getId())).getFirstName());
        adapter.modifyUser(modifiedClient);
        assertEquals("Artur", ((Client) adapter.getUserById(testClient.getId())).getFirstName());
    }

    @Test
    void testModifyUserLoginToOccupiedLogin() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

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
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, repository.readAll().size());

        assertEquals("testLoginKlient", ((Client) adapter.getUserById(testClient.getId())).getLogin());
        assertThrows(UserLoginException.class, () -> adapter.modifyUser(modifiedClient));
        assertEquals("testLoginKlient", ((Client) adapter.getUserById(testClient.getId())).getLogin());

    }

    @Test
    void testActivateUser() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

        testClient.setArchive(true);
        testClientEntity = ClientMapper.toUserEntity(testClient);
        testClient.setArchive(false);
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toUserEntity(testClient));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, repository.readAll().size());

        assertTrue(adapter.getUserById(testClient.getId()).isArchive());
        adapter.activateUser(testClient.getId());
        assertFalse(adapter.getUserById(testClient.getId()).isArchive());
    }

    @Test
    void testDeactivateUser() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testClientEntity2); return testClientEntity2;})
                .then(i -> {users.add(testClientEntity3); return testClientEntity3;});

        Mockito.when(repository.readAll()).thenReturn(users);

        testClient.setArchive(false);
        testClientEntity = ClientMapper.toUserEntity(testClient);
        testClient.setArchive(true);
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toUserEntity(testClient));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testClient2);
        adapter.addUser(testClient3);
        assertEquals(3, repository.readAll().size());

        assertFalse(adapter.getUserById(testClient.getId()).isArchive());
        adapter.activateUser(testClient.getId());
        assertTrue(adapter.getUserById(testClient.getId()).isArchive());
    }

}