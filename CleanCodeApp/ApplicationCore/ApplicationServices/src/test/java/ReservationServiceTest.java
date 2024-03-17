import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.courts.Court;
import tks.gv.infrastructure.reservations.ports.*;
import tks.gv.reservations.Reservation;
import tks.gv.reservationservice.ReservationService;
import tks.gv.users.Client;
import tks.gv.userservice.ClientService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    AddReservationPort addReservationPort;
    @Mock
    CheckClientReservationBalancePort checkClientReservationBalancePort;
    @Mock
    DeleteReservationPort deleteReservationPort;
    @Mock
    GetAllArchiveReservationsPort getAllArchiveReservationsPort;
    @Mock
    GetAllClientReservationsPort getAllClientReservationsPort;
    @Mock
    GetAllCurrentReservationsPort getAllCurrentReservationsPort;
    @Mock
    GetClientCurrentReservationsPort getClientCurrentReservationsPort;
    @Mock
    GetClientEndedReservationsPort getClientEndedReservationsPort;
    @Mock
    GetCourtCurrentReservationPort getCourtCurrentReservationPort;
    @Mock
    GetCourtEndedReservationPort getCourtEndedReservationPort;
    @Mock
    GetReservationByIdPort getReservationByIdPort;
    @Mock
    ReturnCourtPort returnCourtPort;

    @InjectMocks
    final ReservationService reservationService = new ReservationService();

    Reservation getTestReservation;
    Reservation testReservation1;
    Reservation testReservation2;
    Reservation testReservation3;
    Reservation testReservation4;


    Client testClient1;
    Client testClient2;
    Client testClient3;

    String UserUUID1 = "6d4eaba3-7277-4364-9d77-0030ca3d9529";
    String UserUUID2 = "1e83883c-4d8f-46ad-b6b9-19d125d5599f";
    String UserUUID3 = "e7e364a9-0183-4f46-b8c0-7ab248adbc1d";

    String CourtUUID1 = "7a3eda80-be8e-4ff6-a788-18da81387a87";

    LocalDateTime rightNow1 = LocalDateTime.now();

    Client client1;
    Client client2;
    Client client3;
    Client client4;
    Client testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testKlient", testClientPass, testClientType);


    Court court1;
    Court court2;
    Court court3;
    Court court4;



    @BeforeEach
    void init() {

        testReservation1 = new Reservation(UUID.randomUUID(),client1,court1, rightNow1);
        testReservation2 = new Reservation(UUID.randomUUID(),client2, court2, rightNow1);
        testReservation3 = new Reservation(UUID.randomUUID(),testClient, court3, rightNow1 );
        testReservation4 = new Reservation(UUID.randomUUID(),testClient, court4, rightNow1 );
    }

    @Test
    void testCreatingClientManagerNoArgs() {
        ReservationService reservationService = new ReservationService();
        assertNotNull(reservationService);
    }

    @Test
    void testCreatingClientManagerAllArgs() {
        ReservationService reservationService = new ReservationService(addReservationPort, checkClientReservationBalancePort,
                                                                        deleteReservationPort, getAllArchiveReservationsPort,
                                                                        getAllClientReservationsPort, getAllCurrentReservationsPort,
                                                                        getClientCurrentReservationsPort, getClientEndedReservationsPort,
                                                                        getCourtCurrentReservationPort, getCourtEndedReservationPort,
                                                                        getReservationByIdPort, returnCourtPort);
        assertNotNull(reservationService);
    }

    @Test
    void testGetAllCurrentReservations() {
        Mockito.when(getAllCurrentReservationsPort.getAllCurrentReservations()).thenReturn(List.of(testReservation1,testReservation2, testReservation3));

        List<Reservation> reservationList = reservationService.getAllCurrentReservations();
        assertEquals(reservationList.size(), 3);
        assertEquals(testReservation1, reservationList.get(0));
        assertEquals(testReservation2, reservationList.get(1));
        assertEquals(testReservation3, reservationList.get(2));
    }

//    @Test
//    void testAddReservation() {
//        Mockito.when(addReservationPort.addReservation(UserUUID1, CourtUUID1, rightNow1)).thenReturn(testReservation);
//
//        assertEquals(testReservation1, reservationService.addReservation(testReservation));
//    }

    @Test
    void testGetReservationByIdNull() {
        Mockito.when(getReservationByIdPort.getReservationById(any())).thenReturn(null);

        assertNull(reservationService.getReservationById(UUID.randomUUID()));
    }

    @Test
    void testGetReservationById() {
        Mockito.when(getAllCurrentReservationsPort.getAllCurrentReservations()).thenReturn(List.of(testReservation1,testReservation2, testReservation3));
        Mockito.when(getReservationByIdPort.getReservationById(testReservation1.getId())).thenReturn(testReservation1);

        List<Reservation> reservationList = reservationService.getAllCurrentReservations();
        assertEquals(reservationList.size(), 3);

        assertEquals(testReservation1, reservationService.getReservationById(reservationList.get(0).getId()));
    }

//    @Test
//    void testGetAllClientReservations(){
//        Mockito.when(getAllClientReservationsPort.getAllClientReservations(testClient.getId())).thenReturn(List.of(testReservation3,testReservation4));
//
//        List<Reservation> reservationList = reservationService.getAllClientReservations(testClient.getId());
//        assertEquals(reservationList.size(), 2);
//        assertEquals(testReservation3, reservationList.get(0));
//        assertEquals(testReservation4, reservationList.get(1));
//
//    }
    @Test
    void testDeleteReservations() {
        Mockito.doNothing().when(deleteReservationPort).deleteReservation(any(UUID.class));
        reservationService.deleteReservation(testReservation1.getId());
        Mockito.verify(deleteReservationPort, Mockito.times(1)).deleteReservation(testReservation1.getId());
    }

}

//package unittests.servicesTests;
//
//import com.mongodb.client.model.Filters;
//import tks.gv.exceptions.MultiReservationException;
//import tks.gv.users.Client;
//import tks.gv.model.logic.courts.Court;
//import tks.gv.exceptions.UserException;
//import tks.gv.exceptions.CourtException;
//import tks.gv.exceptions.ReservationException;
//import tks.gv.repositories.UserMongoRepository;
//import tks.gv.data.repositories.CourtMongoRepository;
//import tks.gv.data.repositories.ReservationMongoRepository;
//import tks.gv.restapi.data.dto.ReservationDTO;
//import tks.gv.restapi.services.ReservationService;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.time.Month;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//public class ReservationServiceTest {
//
//    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
//    static final UserMongoRepository clientRepository = new UserMongoRepository();
//    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
//    static final ReservationService rm = new ReservationService(reservationRepository);
//
//    String testClientType;
//
//    Client testClient1;
//    Client testClient2;
//    Client testClient3;
//    Court testCourt1;
//    Court testCourt2;
//    Court testCourt3;
//    Court testCourt4;
//    LocalDateTime testTimeStart;
//    LocalDateTime testTimeEnd;
//
//    @BeforeAll
//    @AfterAll
//    static void cleanDB() {
//        reservationRepository.getDatabase().getCollection("users").deleteMany(Filters.empty());
//        reservationRepository.getDatabase().getCollection("courts").deleteMany(Filters.empty());
//        reservationRepository.getDatabase().getCollection("reservations").deleteMany(Filters.empty());
//    }
//
//    @BeforeEach
//    void setUp() {
//        cleanDB();
//        testClientType = "normal";
//
//        testClient1 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "John", "Smith", "12345678901", "123456789", testClientType));
//        testClient2 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "Eva", "Brown", "12345678902", "123456789", testClientType));
//        testClient3 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "Adam", "Long", "12345678903", "123456789", testClientType));
//
//        testCourt1 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 1));
//        testCourt2 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 2));
//        testCourt3 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 3));
//        testCourt4 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 4));
//
//        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
//        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
//    }
//
//    @Test
//    void testCreatingReservationManager() {
//        ReservationService rm = new ReservationService();
//        assertNotNull(rm);
//    }
//
//    @Test
//    void testMakingReservation() {
//        assertEquals(rm.getAllCurrentReservations().size(), 0);
//        assertFalse(testCourt1.isRented());
//
//        ReservationDTO newReservation = rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart);
//
//        assertEquals(rm.getAllCurrentReservations().size(), 1);
//        assertEquals(newReservation, rm.getReservationById(newReservation.getId()));
//        assertTrue(newReservation.getCourt().isRented());
//
//
//        assertFalse(testCourt2.isRented());
//        ReservationDTO newReservation2 = rm.makeReservation(testClient1.getId(), testCourt2.getId());
//
//        assertEquals(rm.getAllCurrentReservations().size(), 2);
//        assertEquals(newReservation2, rm.getReservationById(newReservation2.getId()));
//        assertTrue(newReservation2.getCourt().isRented());
//
//        assertThrows(MultiReservationException.class, () -> rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart));
//        assertEquals(rm.getAllCurrentReservations().size(), 2);
//
//        clientRepository.update(testClient2.getId(), "archive", true);
//        assertFalse(testCourt3.isRented());
//        assertThrows(UserException.class, () -> rm.makeReservation(testClient2.getId(), testCourt3.getId(), testTimeStart));
//        assertEquals(rm.getAllCurrentReservations().size(), 2);
//
//        courtRepository.update(testCourt4.getId(), "archive", true);
//        assertThrows(CourtException.class, () -> rm.makeReservation(testClient1.getId(), testCourt4.getId(), testTimeStart));
//        assertEquals(rm.getAllCurrentReservations().size(), 2);
//    }
//
//    @Test
//    void testCreatingReservationManagerWithNullDate() {
//        assertEquals(0, rm.getAllCurrentReservations().size());
//        ReservationDTO newReservation = rm.makeReservation(testClient1.getId(), testCourt1.getId());
//        var reservations = rm.getAllCurrentReservations();
//        assertEquals(1, reservations.size());
//        assertEquals(newReservation, reservations.get(0));
//    }
//
//    @Test
//    void testEndReservation() {
//        rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart);
//        rm.makeReservation(testClient2.getId(), testCourt2.getId(), testTimeStart);
//
//        assertEquals(0, rm.getAllArchiveReservations().size());
//        assertEquals(2, rm.getAllCurrentReservations().size());
//
//        rm.returnCourt(testCourt1.getId(), testTimeEnd);
//
//        assertEquals(1, rm.getAllArchiveReservations().size());
//        assertEquals(1, rm.getAllCurrentReservations().size());
//        rm.returnCourt(testCourt2.getId());
//
//        assertEquals(2, rm.getAllArchiveReservations().size());
//        assertEquals(0, rm.getAllCurrentReservations().size());
//
//        assertThrows(ReservationException.class, () -> rm.returnCourt(testCourt3.getId()));
//    }
//
//
//    @Test
//    void testCheckingClientBalance() {
//        var testSuperTimeEnd = LocalDateTime.of(2023, Month.JUNE, 5, 12, 0);
//        var testSuperTimeEnd2 = LocalDateTime.of(2023, Month.JUNE, 6, 12, 0);
//
//        rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart);
//        rm.makeReservation(testClient1.getId(), testCourt2.getId(), testTimeStart);
//        rm.makeReservation(testClient1.getId(), testCourt3.getId(), testTimeStart);
//
//        assertEquals(0, rm.checkClientReservationBalance(testClient1.getId()));
//        rm.returnCourt(testCourt1.getId(), testTimeEnd);
//        assertEquals(300, rm.checkClientReservationBalance(testClient1.getId()));
//
//        rm.returnCourt(testCourt2.getId(), testSuperTimeEnd);
//        assertEquals(3750, rm.checkClientReservationBalance(testClient1.getId()));
//
//        rm.returnCourt(testCourt3.getId(), testSuperTimeEnd2);
//        assertEquals(10800, rm.checkClientReservationBalance(testClient1.getId()));
//    }
//}
