package repositoriesTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tks.gv.userservice.data.entities.AdminEntity;
import tks.gv.userservice.data.entities.ClientEntity;
import tks.gv.userservice.data.entities.ResourceAdminEntity;
import tks.gv.userservice.data.entities.UserEntity;
import tks.gv.userservice.data.mappers.entities.AdminMapper;
import tks.gv.userservice.data.mappers.entities.ClientMapper;
import tks.gv.userservice.data.mappers.entities.ResourceAdminMapper;
import tks.gv.userservice.exceptions.MyMongoException;
import tks.gv.userservice.exceptions.UnexpectedUserTypeException;
import tks.gv.userservice.exceptions.UserLoginException;
import tks.gv.userservice.repositories.UserMongoRepository;
import tks.gv.userservice.Admin;
import tks.gv.userservice.Client;
import tks.gv.userservice.ResourceAdmin;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserMongoRepositoryTest extends SetupTestContainer {
    static UserMongoRepository clientRepository;
    ClientEntity client1;
    ClientEntity client2;
    ClientEntity client3;

    private MongoCollection<ClientEntity> getTestCollection() {
        return clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName(), ClientEntity.class);
    }

    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        clientRepository.getDatabase().getCollection("users").deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        clientRepository = new UserMongoRepository(mongoClient, mongoDatabase);

        cleanFirstAndLastTimeDB();
        client1 = ClientMapper.toUserEntity(new Client(UUID.randomUUID(), "Adam", "Smith", "12345678901", "12345678901"));
        client2 = ClientMapper.toUserEntity(new Client(UUID.randomUUID(), "Eva", "Smith", "12345678902", "12345678902"));
        client3 = ClientMapper.toUserEntity(new Client(UUID.randomUUID(), "John", "Lenon", "12345678903", "12345678903"));
    }

    @Test
    void testCreatingRepository() {
        UserMongoRepository clientRepository = new UserMongoRepository(mongoClient, mongoDatabase);
        assertNotNull(clientRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(UserLoginException.class, () -> clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBWithNullId() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertNotNull(clientRepository.create(
                new ClientEntity(null, "Adam", "Niezgodka", "adasNiezg", "Haslo1234!", false)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "adasNiezg"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );

        assertNotNull(clientRepository.create(
                new AdminEntity(null, "Tobiasz", "Trabka", "tobiaszTrab", "Haslo1234!", false)));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "tobiaszTrab"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );


        assertNotNull(clientRepository.create(
                new ResourceAdminEntity(null, "Tobiasz", "Trabka", "tobiaszTrabRes", "Haslo1234!", false)));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "tobiaszTrabRes"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );
    }

    @Test
    void testAddingNewDocumentToDBWithEmptyStringId() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertNotNull(clientRepository.create(
                new ClientEntity("", "Adam", "Niezgodka", "adasNiezg", "Haslo1234!", false)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "adasNiezg"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );

        assertNotNull(clientRepository.create(
                new AdminEntity("", "Tobiasz", "Trabka", "tobiaszTrab", "Haslo1234!", false)));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "tobiaszTrab"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );


        assertNotNull(clientRepository.create(
                new ResourceAdminEntity("", "Tobiasz", "Trabka", "tobiaszTrabRes", "Haslo1234!", false)));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "tobiaszTrabRes"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );
    }

    @Test
    void testAddingNewDocumentToDBWithEmptyStringIdNewUserType() {
        class NewUserEnt extends UserEntity {
            public NewUserEnt(String id, String firstName, String lastName, String login, String password, boolean archive) {
                super(id, firstName, lastName, login, password, archive);
            }
        }

        assertThrows(UnexpectedUserTypeException.class,
                () -> clientRepository.create(new NewUserEnt("", "Tobiasz", "Trabka", "tobiaszTrabRes", "Haslo1234!", false)));
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = (ClientEntity) clientRepository.create(this.client1);
        ClientEntity client2 = (ClientEntity) clientRepository.create(this.client2);
        ClientEntity client3 = (ClientEntity) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList1 = clientRepository.read(Filters.eq("firstname", "John"));
        assertEquals(1, clientsList1.size());
        assertEquals(client3, clientsList1.get(0));

        var clientsList2 = clientRepository.read(Filters.eq("lastname", "Smith"));
        assertEquals(2, clientsList2.size());
        assertEquals(client1, clientsList2.get(0));
        assertEquals(client2, clientsList2.get(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = clientRepository.read(Filters.eq("firstname", "John"));
        assertEquals(0, clientsList.size());
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = (ClientEntity) clientRepository.create(this.client1);
        ClientEntity client2 = (ClientEntity) clientRepository.create(this.client2);
        ClientEntity client3 = (ClientEntity) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = clientRepository.readAll();
        assertEquals(3, clientsList.size());
        assertEquals(client1, clientsList.get(0));
        assertEquals(client2, clientsList.get(1));
        assertEquals(client3, clientsList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = (ClientEntity) clientRepository.create(this.client1);
        clientRepository.create(this.client2);
        ClientEntity client3 = (ClientEntity) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        UserEntity clMapper1 = clientRepository.readByUUID(UUID.fromString(client1.getId()));
        assertNotNull(clMapper1);
        assertEquals(client1, clMapper1);

        UserEntity clMapper3 = clientRepository.readByUUID(UUID.fromString(client3.getId()));
        assertNotNull(clMapper3);
        assertEquals(client3, clMapper3);
    }

    @Test
    void testFindingByUUIDNeg() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        clientRepository.create(this.client2);
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertNull(clientRepository.readByUUID(UUID.randomUUID()));
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = (ClientEntity) clientRepository.create(this.client1);
        ClientEntity client2 = (ClientEntity) clientRepository.create(this.client2);
        ClientEntity client3 = (ClientEntity) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(clientRepository.delete(UUID.fromString(client2.getId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        //Check the rest
        var clientMappersList = clientRepository.readAll();
        assertEquals(2, clientMappersList.size());
        assertEquals(client1, clientMappersList.get(0));
        assertEquals(client3, clientMappersList.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertNotNull(clientRepository.create(client2));
        ClientEntity client = (ClientEntity) clientRepository.create(client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(clientRepository.delete(UUID.fromString(client.getId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        assertFalse(clientRepository.delete(UUID.fromString(client.getId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = (ClientEntity) clientRepository.create(this.client1);
        ClientEntity client2 = (ClientEntity) clientRepository.create(this.client2);
        assertNotNull(clientRepository.create(this.client3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals("Adam",
                ((ClientEntity) clientRepository.readByUUID(UUID.fromString(client1.getId()))).getFirstName());
        assertTrue(clientRepository.update(UUID.fromString(client1.getId()),
                "firstname", "Chris"));
        assertEquals("Chris",
                ((ClientEntity) clientRepository.readByUUID(UUID.fromString(client1.getId()))).getFirstName());

        //Test adding new value to document
        assertFalse(clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", client2.getId()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertTrue(clientRepository.update(UUID.fromString(client2.getId()),
                "field", "newValue"));

        assertTrue(clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", client2.getId()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertEquals("newValue",
                clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                        .find(Filters.eq("_id", client2.getId()))
                        .into(new ArrayList<>()).get(0).getString("field"));
    }

    @Test
    void testUpdatingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class,
                () -> clientRepository.update(UUID.fromString(client3.getId()),
                        "_id", UUID.randomUUID().toString()));

        assertFalse(clientRepository.update(UUID.randomUUID(), "firstname", "Harry"));
    }

    @Test
    void testUpdateByReplace() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        clientRepository.create(client1);
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(client1, clientRepository.readByUUID(UUID.fromString(client1.getId())));

        ClientEntity clientEntity = new ClientEntity(client1.getId(), "AAA", "BBB",
                "loginek2134124", "Haslo1234!", false);

        assertTrue(clientRepository.updateByReplace(UUID.fromString(client1.getId()), clientEntity));

        assertNotEquals(client1, clientRepository.readByUUID(UUID.fromString(client1.getId())));
        assertEquals(clientEntity, clientRepository.readByUUID(UUID.fromString(client1.getId())));
    }

    @Test
    void testGetCollectionNameMethod() {
        //Get collection name
        assertEquals("users", clientRepository.getCollectionName());
    }

    @Test
    void testAddingNewDocumentAdminToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(
                AdminMapper.toUserEntity(new Admin(UUID.randomUUID(), "Tobiasz", "Trabka", "testowyAdmin", "Haslo1234!"))));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(
                AdminMapper.toUserEntity(new Admin(UUID.randomUUID(), "Karl", "Scout", "testowyAdmin2", "Haslo1234!"))));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testReadingDocumentAdminFromDB() {
        // Adding Admin document
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        AdminEntity testAdminEnt = AdminMapper.toUserEntity(
                new Admin(UUID.randomUUID(), "Tobiasz", "Trabka", "testowyAdmin", "Haslo1234!"));
        assertNotNull(clientRepository.create(testAdminEnt));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        // Test reading
        assertEquals(testAdminEnt,
                (AdminEntity) clientRepository.read(Filters.eq("login", "testowyAdmin")).get(0));
    }

    @Test
    void testAddingNewDocumentResAdminToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(
                ResourceAdminMapper.toUserEntity(new ResourceAdmin(UUID.randomUUID(), "Tobiasz", "Trabka", "testowyResAdmin", "Haslo1234!"))));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(
                ResourceAdminMapper.toUserEntity(new ResourceAdmin(UUID.randomUUID(), "Karl", "Key", "testowyResAdmin2", "Haslo1234!"))));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testReadingDocumentResAdminFromDB() {
        // Adding Admin document
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ResourceAdminEntity testResAdminEnt = ResourceAdminMapper.toUserEntity(
                new ResourceAdmin(UUID.randomUUID(), "Tobiasz", "Trabka", "testowyResAdmin", "Haslo1234!"));
        assertNotNull(clientRepository.create(testResAdminEnt));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        // Test reading
        assertEquals(testResAdminEnt,
                clientRepository.read(Filters.eq("login", "testowyResAdmin")).get(0));
    }

    @Test
    void testCreatingNewCollection() {
        getTestCollection().drop();
        assertNotNull(new UserMongoRepository(mongoClient, mongoDatabase));
    }

}
