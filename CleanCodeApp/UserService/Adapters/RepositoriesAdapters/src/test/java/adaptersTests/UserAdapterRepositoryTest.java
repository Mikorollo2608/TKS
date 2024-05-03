package adaptersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.userservice.aggregates.UserMongoRepositoryAdapter;
import tks.gv.userservice.data.entities.UserEntity;
import tks.gv.userservice.data.mappers.entities.AdminMapper;
import tks.gv.userservice.data.mappers.entities.ClientMapper;
import tks.gv.userservice.data.mappers.entities.ResourceAdminMapper;
import tks.gv.userservice.exceptions.MyMongoException;
import tks.gv.userservice.exceptions.RepositoryAdapterException;
import tks.gv.userservice.exceptions.UnexpectedUserTypeException;
import tks.gv.userservice.exceptions.UserLoginException;
import tks.gv.userservice.repositories.UserMongoRepository;
import tks.gv.userservice.Admin;
import tks.gv.userservice.Client;
import tks.gv.userservice.ResourceAdmin;
import tks.gv.userservice.User;

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
    Admin testAdmin;
    ResourceAdmin testResourceAdmin;
    UserEntity testClientEntity;
    UserEntity testAdminEntity;
    UserEntity testResourceAdminEntity;

    @BeforeEach
    void init() {
        testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testLoginKlient", "Haslo1234!", "normal");
        testAdmin = new Admin(UUID.randomUUID(), "testLoginAdmin", "Haslo1234!");
        testResourceAdmin = new ResourceAdmin(UUID.randomUUID(), "testResAdmin", "Haslo1234!");

        testClientEntity = ClientMapper.toUserEntity(testClient);
        testAdminEntity = AdminMapper.toUserEntity(testAdmin);
        testResourceAdminEntity = ResourceAdminMapper.toUserEntity(testResourceAdmin);
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
    void testAddAdmin() {
        Mockito.when(repository.create(eq(AdminMapper.toUserEntity(testAdmin)))).thenReturn(AdminMapper.toUserEntity(testAdmin));
        adapter.addUser(testAdmin);
        Mockito.verify(repository, Mockito.times(1)).create(AdminMapper.toUserEntity(testAdmin));
    }

    @Test
    void testAddResourceAdmin() {
        Mockito.when(repository.create(eq(ResourceAdminMapper.toUserEntity(testResourceAdmin)))).thenReturn(ResourceAdminMapper.toUserEntity(testResourceAdmin));
        adapter.addUser(testResourceAdmin);
        Mockito.verify(repository, Mockito.times(1)).create(ResourceAdminMapper.toUserEntity(testResourceAdmin));
    }

    @Test
    void testAddNewUserType() {
        class NewUser extends User {
        }

        assertThrows(UnexpectedUserTypeException.class, () -> adapter.addUser(new NewUser()));
        Mockito.verify(repository, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void testAddNeg() {
        Mockito.when(repository.create(Mockito.any())).thenThrow(new MyMongoException("Test exception"));
        assertThrows(RepositoryAdapterException.class, () -> adapter.addUser(testAdmin));
    }

    @Test
    void testGetAllUsers() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        assertEquals(0, users.size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, users.size());

        List<User> userList = adapter.getAllUsers();
        assertEquals(3, userList.size());
        assertEquals(testClient, userList.get(0));
        assertEquals(testAdmin, userList.get(1));
        assertEquals(testResourceAdmin, userList.get(2));
    }

    @Test
    void testGetUserById() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity);

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        assertEquals(testClient, adapter.getUserById(testClient.getId()));
    }

    @Test
    void testGetUserByLogin() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testAdminEntity));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        assertEquals(testAdmin, adapter.getUserByLogin(testAdmin.getLogin()));
    }

    @Test
    void testGetUserByLoginMatching() {
        List<UserEntity> users = new ArrayList<>();

        Mockito.when(repository.create(Mockito.any()))
                .then(i -> {users.add(testClientEntity); return testClientEntity;})
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testClientEntity, testAdminEntity));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        List<User> userMatchingList = adapter.getUserByLoginMatching("testLogin");
        assertEquals(2, userMatchingList.size());
        assertEquals(testClient, userMatchingList.get(0));
        assertEquals(testAdmin, userMatchingList.get(1));
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
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of());
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toUserEntity(modifiedClient));
        Mockito.when(repository.updateByReplace(Mockito.any(),Mockito.any())).thenReturn(true);

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
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
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        Mockito.when(repository.read(Mockito.any())).thenReturn(List.of(testClientEntity));
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity);

        Client modifiedClient = new Client(
                testClient.getId(),
                testClient.getFirstName(),
                testClient.getLastName(),
                "testLoginAdmin",
                testClient.getPassword(),
                testClient.getClientTypeName()
        );

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
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
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        testClient.setArchive(true);
        testClientEntity = ClientMapper.toUserEntity(testClient);
        testClient.setArchive(false);
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toUserEntity(testClient));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
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
                .then(i -> {users.add(testAdminEntity); return testAdminEntity;})
                .then(i -> {users.add(testResourceAdminEntity); return testResourceAdminEntity;});

        Mockito.when(repository.readAll()).thenReturn(users);

        testClient.setArchive(false);
        testClientEntity = ClientMapper.toUserEntity(testClient);
        testClient.setArchive(true);
        Mockito.when(repository.readByUUID(eq(testClient.getId()))).thenReturn(testClientEntity).thenReturn(ClientMapper.toUserEntity(testClient));

        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        assertFalse(adapter.getUserById(testClient.getId()).isArchive());
        adapter.activateUser(testClient.getId());
        assertTrue(adapter.getUserById(testClient.getId()).isArchive());
    }

}