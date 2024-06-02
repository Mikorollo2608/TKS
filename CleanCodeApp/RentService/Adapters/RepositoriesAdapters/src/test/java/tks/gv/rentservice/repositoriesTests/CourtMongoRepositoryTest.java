package tks.gv.rentservice.repositoriesTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tks.gv.rentservice.data.entities.ClientEntity;
import tks.gv.rentservice.data.entities.CourtEntity;
import tks.gv.rentservice.data.entities.ReservationEntity;
import tks.gv.rentservice.exceptions.CourtNumberException;
import tks.gv.rentservice.exceptions.MyMongoException;
import tks.gv.rentservice.repositories.CourtMongoRepository;
import tks.gv.rentservice.repositories.ReservationMongoRepository;
import tks.gv.rentservice.repositories.ClientMongoRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourtMongoRepositoryTest extends SetupTestContainer {

    static CourtMongoRepository courtRepository;

    CourtEntity court1;
    CourtEntity court2;
    CourtEntity court3;

    private MongoCollection<CourtEntity> getTestCollection() {
        return courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), CourtEntity.class);
    }

    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        courtRepository.getDatabase().getCollection("clients").deleteMany(Filters.empty());
        courtRepository.getDatabase().getCollection("courts").deleteMany(Filters.empty());
        courtRepository.getDatabase().getCollection("reservations").deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        courtRepository = new CourtMongoRepository(mongoClient, mongoDatabase);

        cleanFirstAndLastTimeDB();
        court1 = new CourtEntity(UUID.randomUUID().toString(), 100, 200, 1, false, 0);
        court2 = new CourtEntity(UUID.randomUUID().toString(), 200, 200, 2, false, 0);
        court3 = new CourtEntity(UUID.randomUUID().toString(), 300, 300, 3, false, 0);
    }

//    @AfterEach
//    void reconnect() {
//        if (//if client is closed)
//        mongoClient = dbconfig.mongoClient();
//        mongoDatabase = dbconfig.mongoDatabase(mongoClient);
//        courtRepository = new CourtMongoRepository(mongoClient, mongoDatabase);
//    }

    @Test
    void testCreatingRepository() {
        CourtMongoRepository courtRepository = new CourtMongoRepository(mongoClient, mongoDatabase);
        assertNotNull(courtRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        court1 = new CourtEntity("", 100, 200, 1, false, 0);
        court2 = new CourtEntity(null, 200, 200, 2, false, 0);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentWithoutIdToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(CourtNumberException.class, () -> courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        CourtEntity court1 = courtRepository.create(this.court1);
        CourtEntity court2 = courtRepository.create(this.court2);
        CourtEntity court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var courtsList1 = courtRepository.read(Filters.eq("area", 300));
        assertEquals(1, courtsList1.size());
        assertEquals(court3, courtsList1.get(0));

        var clientsList2 = courtRepository.read(Filters.eq("basecost", 200));
        assertEquals(2, clientsList2.size());
        assertEquals(court1, clientsList2.get(0));
        assertEquals(court2, clientsList2.get(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = courtRepository.read(Filters.eq("area", 999));
        assertEquals(0, clientsList.size());
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        CourtEntity court1 = courtRepository.create(this.court1);
        CourtEntity court2 = courtRepository.create(this.court2);
        CourtEntity court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = courtRepository.readAll();
        assertEquals(3, clientsList.size());
        assertEquals(court1, clientsList.get(0));
        assertEquals(court2, clientsList.get(1));
        assertEquals(court3, clientsList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        CourtEntity court1 = courtRepository.create(this.court1);
        assertNotNull(courtRepository.create(this.court2));
        CourtEntity court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        CourtEntity couMapper1 = courtRepository.readByUUID(UUID.fromString(court1.getId().toString()));
        assertNotNull(couMapper1);
        assertEquals(court1, couMapper1);

        CourtEntity couMapper3 = courtRepository.readByUUID(UUID.fromString(court3.getId().toString()));
        assertNotNull(couMapper3);
        assertEquals(court3, couMapper3);
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        CourtEntity court1 = courtRepository.create(this.court1);
        CourtEntity court2 = courtRepository.create(this.court2);
        CourtEntity court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(courtRepository.delete(UUID.fromString(court2.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        //Check the rest
        var courtMappersLists = courtRepository.readAll();
        assertEquals(2, courtMappersLists.size());
        assertEquals(court1, courtMappersLists.get(0));
        assertEquals(court3, courtMappersLists.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(this.court1));
        assertNotNull(courtRepository.create(this.court2));
        CourtEntity court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(courtRepository.delete(UUID.fromString(court3.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        assertFalse(courtRepository.delete(UUID.fromString(court3.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testDeletingDocumentsInDBExistingAllocation() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        LocalDateTime testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);

        CourtEntity testCourt1 = courtRepository.create(new CourtEntity(UUID.randomUUID().toString(), 1000, 100, 1, false, 0));
        assertNotNull(courtRepository.create(this.court2));
        assertNotNull(courtRepository.create(this.court3));

        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        ReservationMongoRepository reservationMongoRepository = new ReservationMongoRepository(mongoClient, mongoDatabase);
        ClientMongoRepository clientMongoRepository = new ClientMongoRepository(mongoClient, mongoDatabase);
        ClientEntity testClient1 = clientMongoRepository.create(new ClientEntity(UUID.randomUUID().toString(),
                "999999999999", false, "normal"));
        ReservationEntity testReservation1 = new ReservationEntity(UUID.randomUUID().toString(), testClient1.getId(),
                testCourt1.getId(), testTimeStart, null, 0);
        reservationMongoRepository.create(testReservation1);
        assertThrows(MyMongoException.class, () -> courtRepository.delete(UUID.fromString(testCourt1.getId())));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        reservationMongoRepository.delete(UUID.fromString(testReservation1.getId()));

    }

    @Test
    void testUpdatingRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        CourtEntity court1 = courtRepository.create(this.court1);
        CourtEntity court2 = courtRepository.create(this.court2);
        assertNotNull(courtRepository.create(this.court3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(200,
                courtRepository.readByUUID(UUID.fromString(court1.getId().toString())).getBaseCost());
        assertTrue(courtRepository.update(UUID.fromString(court1.getId().toString()),
                "basecost", 350));
        assertEquals(350,
                courtRepository.readByUUID(UUID.fromString(court1.getId().toString())).getBaseCost());

        //Test adding new value to document
        assertFalse(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", court2.getId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertTrue(courtRepository.update(UUID.fromString(court2.getId().toString()),
                "field", "newValue"));

        assertTrue(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", court2.getId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertEquals("newValue",
                courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                        .find(Filters.eq("_id", court2.getId().toString()))
                        .into(new ArrayList<>()).get(0).getString("field"));
    }

    @Test
    void testUpdatingRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(this.court1));
        assertNotNull(courtRepository.create(this.court2));
        CourtEntity court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class,
                () -> courtRepository.update(UUID.fromString(court3.getId().toString()),
                        "_id", UUID.randomUUID().toString()));

        assertFalse(courtRepository.update(UUID.randomUUID(), "area", 435.0));
    }

    @Test
    void testUpdatingWholeRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        CourtEntity court1 = courtRepository.create(this.court1);
        assertNotNull(courtRepository.create(this.court2));
        assertNotNull(courtRepository.create(this.court3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        CourtEntity replacementCourt = new CourtEntity(court1.getId(), 111, 123, court1.getCourtNumber(), court1.isArchive(), court1.getRented());
        assertTrue(courtRepository.updateByReplace(UUID.fromString(court1.getId()), replacementCourt));
        CourtEntity court1Copy = courtRepository.readByUUID(UUID.fromString(court1.getId()));
        assertEquals(court1Copy, replacementCourt);
    }

    @Test
    void testUpdatingWholeRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertNotNull(courtRepository.create(this.court2));
        assertNotNull(courtRepository.create(this.court3));

        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        CourtEntity replacementCourt = new CourtEntity(court1.getId(), 111, 123, court1.getCourtNumber(), court1.isArchive(), court1.getRented());

        assertFalse(courtRepository.updateByReplace(UUID.fromString(court1.getId()), replacementCourt));
    }
}
