package repositoriesTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tks.gv.aggregates.UserMongoRepositoryAdapter;

import tks.gv.data.entities.AdminEntity;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.entities.ResourceAdminEntity;
import tks.gv.data.entities.UserEntity;
import tks.gv.data.mappers.entities.AdminMapper;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.data.mappers.entities.ResourceAdminMapper;

import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.RepositoryAdapterException;
import tks.gv.exceptions.UnexpectedUserTypeException;
import tks.gv.repositories.UserMongoRepository;

import tks.gv.users.Admin;
import tks.gv.users.Client;
import tks.gv.users.ResourceAdmin;
import tks.gv.users.User;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserAdapterRepositoryTest {
    static final UserMongoRepository repository = new UserMongoRepository();
    static final UserMongoRepositoryAdapter adapter = new UserMongoRepositoryAdapter(repository);

    Client testClient;
    Admin testAdmin;
    ResourceAdmin testResourceAdmin;

    @BeforeEach
    void init() {
        testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testLoginKlient", "Haslo1234!", "normal");
        testAdmin = new Admin(UUID.randomUUID(), "testLoginAdmin", "Haslo1234!");
        testResourceAdmin = new ResourceAdmin(UUID.randomUUID(), "testResAdmin", "Haslo1234!");
    }

    @AfterEach
    void clear() {
        repository.delete(testClient.getId());
        repository.delete(testAdmin.getId());
        repository.delete(testResourceAdmin.getId());
    }

    @Test
    void testConstructingAdapter() {
        UserMongoRepositoryAdapter testAdapt = new UserMongoRepositoryAdapter(repository);
        assertNotNull(testAdapt);
    }

    @Test
    void testAddClient() {
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        assertEquals(1, repository.readAll().size());
        assertEquals(testClient,
                ClientMapper.fromUserEntity((ClientEntity) repository.readByUUID(testClient.getId())));
    }

    @Test
    void testAddAdmin() {
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testAdmin);
        assertEquals(1, repository.readAll().size());
        assertEquals(testAdmin,
                AdminMapper.fromUserEntity((AdminEntity) repository.readByUUID(testAdmin.getId())));
    }

    @Test
    void testAddResourceAdmin() {
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testResourceAdmin);
        assertEquals(1, repository.readAll().size());
        assertEquals(testResourceAdmin,
                ResourceAdminMapper.fromUserEntity((ResourceAdminEntity) repository.readByUUID(testResourceAdmin.getId())));
    }

    @Test
    void testAddNewUserType() {
        class NewUser extends User {}

        assertEquals(0, repository.readAll().size());
        assertThrows(UnexpectedUserTypeException.class, () -> adapter.addUser(new NewUser()));
        assertEquals(0, repository.readAll().size());
    }

    @Test
    void testAddNeg() {
        class UserMongoRepositoryExt extends UserMongoRepository {
            @Override
            public UserEntity create(UserEntity initUser) {
                throw new MyMongoException("TestException");
            }
        }

        UserMongoRepository repositoryTest = new UserMongoRepositoryExt();
        UserMongoRepositoryAdapter adapterTest = new UserMongoRepositoryAdapter(repositoryTest);
        assertEquals(0, repositoryTest.readAll().size());
        assertThrows(RepositoryAdapterException.class, () -> adapterTest.addUser(testAdmin));
        assertEquals(0, repositoryTest.readAll().size());
    }

    @Test
    void testGetAllUsers() {
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        List<User> userList = adapter.getAllUsers();
        assertEquals(3, userList.size());
        assertEquals(testClient, userList.get(0));
        assertEquals(testAdmin, userList.get(1));
        assertEquals(testResourceAdmin, userList.get(2));
    }

    @Test
    void testGetUserById() {
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        assertEquals(testClient, adapter.getUserById(testClient.getId()));
    }

    @Test
    void testGetUserByLogin() {
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        assertEquals(testAdmin, adapter.getUserByLogin(testAdmin.getLogin()));
    }

    @Test
    void testGetUserByLoginMatching() {
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
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());
        Client modifiedClient = new Client(
                testClient.getId(),
                "Artur",
                testClient.getLastName(),
                testClient.getLogin(),
                testClient.getPassword(),
                testClient.getClientTypeName()
        );

        assertEquals("Adam", ((Client) adapter.getUserById(testClient.getId())).getFirstName());
        assertThrows(UnsupportedOperationException.class, () -> adapter.modifyUser(modifiedClient));
//        assertEquals("Artur", ((Client) adapter.getUserById(testClient.getId())).getFirstName());

    }

    @Test
    void testActivateUser() {
        testClient.setArchive(true);
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        assertTrue(adapter.getUserById(testClient.getId()).isArchive());
        assertThrows(UnsupportedOperationException.class, () -> adapter.activateUser(testClient.getId()));
//        assertFalse(adapter.getUserById(testClient.getId()).isArchive());
    }

    @Test
    void testDeactivateUser() {
        assertEquals(0, repository.readAll().size());
        adapter.addUser(testClient);
        adapter.addUser(testAdmin);
        adapter.addUser(testResourceAdmin);
        assertEquals(3, repository.readAll().size());

        assertFalse(adapter.getUserById(testClient.getId()).isArchive());
        assertThrows(UnsupportedOperationException.class, () -> adapter.deactivateUser(testClient.getId()));
//        assertTrue(adapter.getUserById(testClient.getId()).isArchive());
    }

}
