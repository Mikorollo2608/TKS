package repositoriesTests;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import tks.gv.courts.Court;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.entities.ReservationEntity;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.data.mappers.entities.CourtMapper;
import tks.gv.data.mappers.entities.ReservationMapper;
import tks.gv.exceptions.MultiReservationException;
import tks.gv.repositories.CourtMongoRepository;
import tks.gv.repositories.ReservationMongoRepository;
import tks.gv.repositories.config.DBConfig;
import tks.gv.reservations.Reservation;
import tks.gv.users.Client;

import tks.gv.exceptions.UserException;
import tks.gv.exceptions.CourtException;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.ReservationException;
import tks.gv.repositories.UserMongoRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationMongoRepositoryTest {
    static final DBConfig dbconfig = new DBConfig();
    static MongoClient mongoClient = dbconfig.mongoClient();
    static MongoDatabase mongoDatabase = dbconfig.mongoDatabase(mongoClient);

    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository(mongoClient, mongoDatabase);
    static final CourtMongoRepository courtRepository = new CourtMongoRepository(mongoClient, mongoDatabase);
    static final UserMongoRepository clientRepository = new UserMongoRepository(mongoClient, mongoDatabase);
    String testClientType;

    Client testClient1;
    Client testClient2;
    Client testClient3;
    Court testCourt1;
    Court testCourt2;
    Court testCourt3;
    Court testCourt4;
    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    private MongoCollection<ReservationEntity> getTestCollection() {
        return reservationRepository.getDatabase()
                .getCollection(reservationRepository.getCollectionName(), ReservationEntity.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanDB() {
       mongoClient = dbconfig.mongoClient();
       mongoDatabase = dbconfig.mongoDatabase(mongoClient);

        reservationRepository.getDatabase().getCollection("users").deleteMany(Filters.empty());
        reservationRepository.getDatabase().getCollection("courts").deleteMany(Filters.empty());
        reservationRepository.getDatabase().getCollection("reservations").deleteMany(Filters.empty());
    }

    @BeforeEach
    void setUp() {
        cleanDB();
        testClientType = "normal";

        testClient1 = ClientMapper.fromUserEntity((ClientEntity) clientRepository.create(ClientMapper.toUserEntity(new Client(UUID.randomUUID(), "John", "Smith", "12345678901", "12345678901", testClientType))));
        testClient2 = ClientMapper.fromUserEntity((ClientEntity) clientRepository.create(ClientMapper.toUserEntity(new Client(UUID.randomUUID(), "Eva", "Brown", "12345678902", "12345678902", testClientType))));
        testClient3 = ClientMapper.fromUserEntity((ClientEntity) clientRepository.create(ClientMapper.toUserEntity(new Client(UUID.randomUUID(), "Adam", "Long", "12345678903", "12345678903", testClientType))));

        testCourt1 = CourtMapper.fromMongoCourt(courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.randomUUID(), 1000, 100, 1))));
        testCourt2 = CourtMapper.fromMongoCourt(courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.randomUUID(), 1000, 100, 2))));
        testCourt3 = CourtMapper.fromMongoCourt(courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.randomUUID(), 1000, 100, 3))));
        testCourt4 = CourtMapper.fromMongoCourt(courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.randomUUID(), 1000, 100, 4))));

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingRepository() {
        ReservationMongoRepository reservationRepository = new ReservationMongoRepository(mongoClient, mongoDatabase);
        assertNotNull(reservationRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        Reservation reservation = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toReservationEntity(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        Reservation reservation2 = new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart);
        assertNotNull(reservation2);
        reservationRepository.create(ReservationMapper.toReservationEntity(reservation2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        //Reserve reserved court
        Reservation reservation2 = new Reservation(UUID.randomUUID(), testClient2, testCourt1, testTimeStart);
        assertNotNull(reservation2);
        assertThrows(MultiReservationException.class, () -> reservationRepository.create(ReservationMapper.toReservationEntity(reservation2)));

        //No client in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), new Client(UUID.randomUUID(), "John", "Blade",
                        "12345678911", "12345678911", "normal"), testCourt3, testTimeStart))));

        //No court in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient3,
                        new Court(UUID.randomUUID(), 1000, 100, 5), testTimeStart))));

        //Archive client
        clientRepository.update(testClient3.getId(), "archive", true);
        assertThrows(UserException.class, () -> reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(),
                testClient3, testCourt3, testTimeStart))));

        //Archive court
        courtRepository.update(testCourt4.getId(), "archive", true);
        assertThrows(CourtException.class, () -> reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(),
                testClient2, testCourt4, testTimeStart))));
    }

    @Test
    void testFindingDocumentRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationEntity reservationMapper1 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient1,
                testCourt1, LocalDateTime.of(2000, Month.JUNE, 13, 14, 5))));
        ReservationEntity reservationMapper2 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient2,
                testCourt2, testTimeStart)));
        ReservationEntity reservationMapper3 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient3,
                testCourt3, testTimeStart)));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
                testClient1.getId().toString()));
        assertEquals(1, reservationsList1.size());
        assertEquals(reservationMapper1, reservationsList1.get(0));

        var reservationsList2 = reservationRepository.read(Filters.eq("begintime",
                testTimeStart));
        assertEquals(reservationMapper2, reservationsList2.get(0));
        assertEquals(reservationMapper3, reservationsList2.get(1));
    }

    @Test
    void testFindingDocumentRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservationMapper1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservationRepository.create(ReservationMapper.toReservationEntity(reservationMapper1)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
                testClient2.getId().toString()));
        assertEquals(0, reservationsList1.size());
    }

    @Test
    void testFindingDocumentByUUIDPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationEntity reservationMapper2 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient2,
                testCourt2, testTimeStart)));
        ReservationEntity reservationMapper3 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient3, testCourt3, testTimeStart)));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        var reservation1 = reservationRepository.readByUUID(
                UUID.fromString(reservationMapper2.getId().toString()));
        assertNotNull(reservation1);
        assertEquals(reservationMapper2, reservation1);

        var reservation2 = reservationRepository.readByUUID(
                UUID.fromString(reservationMapper3.getId().toString()));
        assertNotNull(reservation2);
        assertEquals(reservationMapper3, reservation2);
    }

    @Test
    void testFindingByUUIDNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservationMapper1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservationRepository.create(ReservationMapper.toReservationEntity(reservationMapper1)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertNull(reservationRepository.readByUUID(UUID.randomUUID()));
    }

    @Test
    void testFindingAllDocuments() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationEntity reservationMapper1 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart)));
        ReservationEntity reservationMapper2 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart)));
        ReservationEntity reservationMapper3 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient3, testCourt3, testTimeStart)));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList = reservationRepository.readAll();
        assertEquals(3, reservationsList.size());
        assertEquals(reservationMapper1, reservationsList.get(0));
        assertEquals(reservationMapper2, reservationsList.get(1));
        assertEquals(reservationMapper3, reservationsList.get(2));
    }

    @Test
    void testDeletingDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        var reservationMapper1 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart)));
        var reservationMapper2 = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart)));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.delete(UUID.fromString(reservationMapper2.getId()));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.delete(UUID.fromString(reservationMapper1.getId()));
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(NullPointerException.class, () -> reservationRepository.delete(null));
        assertTrue(reservationRepository.delete(UUID.randomUUID()));
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testClassicUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationEntity reservation = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(testClient1.getId().toString(),
                reservationRepository.readByUUID(UUID.fromString(reservation.getId())).getClientId());
        assertTrue(reservationRepository.update(UUID.fromString(reservation.getId()), "clientid",
                testClient2.getId().toString()));
        assertEquals(testClient2.getId().toString(),
                reservationRepository.readByUUID(UUID.fromString(reservation.getId())).getClientId());
    }

    @Test
    void testClassicUpdatingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationEntity reservation = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class, () -> reservationRepository.update(UUID.fromString(reservation.getId()),
                "_id", UUID.randomUUID().toString()));
        assertFalse(reservationRepository.update(UUID.randomUUID(), "clientid",
                testClient2.getId().toString()));
    }

    @Test
    void testEndUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationEntity reservation = reservationRepository.create(ReservationMapper.toReservationEntity(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart)));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertNull(reservationRepository.readByUUID(UUID.fromString(reservation.getId())).getEndTime());
        assertEquals(0, reservationRepository.readByUUID(UUID.fromString(reservation.getId())).getReservationCost());
        reservationRepository.update(UUID.fromString(reservation.getId()), "endtime", testTimeEnd);
        assertEquals(testTimeEnd, reservationRepository.readByUUID(UUID.fromString(reservation.getId())).getEndTime());
//        assertEquals(300, reservationRepository.readByUUID(UUID.fromString(reservation.getId())).getReservationCost());
    }

    @Test
    void testEndUpdatingDocumentsInDBNegative() {
        Reservation reservation = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toReservationEntity(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

//        reservationRepository.update(testCourt1.getId(), testTimeEnd);
//        assertThrows(ReservationException.class, () -> reservationRepository.update(testCourt1.getId(), testTimeEnd));
    }
}
