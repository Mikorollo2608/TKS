package repositoriesTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.ClientLoginException;
import tks.gv.repositories.ClientMongoRepository;
import tks.gv.Client;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientMongoRepositoryTest extends SetupTestContainer {
    static ClientMongoRepository clientRepository;
    ClientEntity client1;
    ClientEntity client2;
    ClientEntity client3;
    final String testClientType = "normal";

    private MongoCollection<ClientEntity> getTestCollection() {
        return clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName(), ClientEntity.class);
    }

    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        clientRepository.getDatabase().getCollection("clients").deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        clientRepository = new ClientMongoRepository(mongoClient, mongoDatabase);

        cleanFirstAndLastTimeDB();
        client1 = ClientMapper.toEntity(new Client(UUID.randomUUID(), "12345678901", testClientType));
        client2 = ClientMapper.toEntity(new Client(UUID.randomUUID(), "12345678902", testClientType));
        client3 = ClientMapper.toEntity(new Client(UUID.randomUUID(), "12345678903", testClientType));
    }

    @Test
    void testCreatingRepository() {
        ClientMongoRepository clientRepository = new ClientMongoRepository(mongoClient, mongoDatabase);
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
        assertThrows(ClientLoginException.class, () -> clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBWithNullId() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertNotNull(clientRepository.create(
                new ClientEntity(null, "adasNiezg", false, "normal")));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "adasNiezg"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );
    }

    @Test
    void testAddingNewDocumentToDBWithEmptyStringId() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertNotNull(clientRepository.create(
                new ClientEntity("", "adasNiezg", false, "normal")));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName())
                .find(Filters.eq("login", "adasNiezg"))
                .into(new ArrayList<>()).get(0)
                .get("_id")
        );
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = clientRepository.create(this.client1);
        ClientEntity client2 = clientRepository.create(this.client2);
        ClientEntity client3 = clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList1 = clientRepository.read(Filters.eq("login", "12345678903"));
        assertEquals(1, clientsList1.size());
        assertEquals(client3, clientsList1.get(0));

        var clientsList2 = clientRepository.read(Filters.eq("login", "12345678902"));
        assertEquals(1, clientsList2.size());
        assertEquals(client2, clientsList2.get(0));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = clientRepository.read(Filters.eq("login", "12345678909"));
        assertEquals(0, clientsList.size());
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = clientRepository.create(this.client1);
        ClientEntity client2 = clientRepository.create(this.client2);
        ClientEntity client3 = clientRepository.create(this.client3);
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
        ClientEntity client1 = clientRepository.create(this.client1);
        clientRepository.create(this.client2);
        ClientEntity client3 = clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        ClientEntity clMapper1 = clientRepository.readByUUID(UUID.fromString(client1.getId()));
        assertNotNull(clMapper1);
        assertEquals(client1, clMapper1);

        ClientEntity clMapper3 = clientRepository.readByUUID(UUID.fromString(client3.getId()));
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
        ClientEntity client1 = clientRepository.create(this.client1);
        ClientEntity client2 = clientRepository.create(this.client2);
        ClientEntity client3 = clientRepository.create(this.client3);
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
        ClientEntity client = clientRepository.create(client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(clientRepository.delete(UUID.fromString(client.getId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        assertFalse(clientRepository.delete(UUID.fromString(client.getId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ClientEntity client1 = clientRepository.create(this.client1);
        ClientEntity client2 = clientRepository.create(this.client2);
        assertNotNull(clientRepository.create(this.client3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals("normal",
                (clientRepository.readByUUID(UUID.fromString(client1.getId()))).getClientType());
        assertTrue(clientRepository.update(UUID.fromString(client1.getId()),
                "clienttype", "athlete"));
        assertEquals("athlete",
                (clientRepository.readByUUID(UUID.fromString(client1.getId()))).getClientType());

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

        assertFalse(clientRepository.update(UUID.randomUUID(), "login", "HarryA"));
    }

    @Test
    void testUpdateByReplace() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        clientRepository.create(client1);
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(client1, clientRepository.readByUUID(UUID.fromString(client1.getId())));

        ClientEntity clientEntity = new ClientEntity(client1.getId(), "loginek2134124",
                false, "normal");

        assertTrue(clientRepository.updateByReplace(UUID.fromString(client1.getId()), clientEntity));

        assertNotEquals(client1, clientRepository.readByUUID(UUID.fromString(client1.getId())));
        assertEquals(clientEntity, clientRepository.readByUUID(UUID.fromString(client1.getId())));
    }

    @Test
    void testGetCollectionNameMethod() {
        //Get collection name
        assertEquals("clients", clientRepository.getCollectionName());
    }

    @Test
    void testCreatingNewCollection() {
        getTestCollection().drop();
        assertNotNull(new ClientMongoRepository(mongoClient, mongoDatabase));
    }

}
