import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.courts.Court;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.ReservationException;
import tks.gv.infrastructure.courts.ports.GetCourtByIdPort;
import tks.gv.infrastructure.courts.ports.ModifyCourtPort;
import tks.gv.infrastructure.reservations.ports.*;
import tks.gv.reservations.Reservation;
import tks.gv.reservationservice.ReservationService;
import tks.gv.userinterface.courts.ports.GetCourtByIdUseCase;
import tks.gv.userinterface.courts.ports.ModifyCourtUseCase;
import tks.gv.users.Client;
import tks.gv.userservice.ClientService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    AddReservationPort addReservationPort;
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
    @Mock
    GetCourtByIdUseCase getCourtByIdUseCase;
    @Mock
    ModifyCourtUseCase modifyCourtUseCase;

    @InjectMocks
    final ReservationService reservationService = new ReservationService();

    Reservation testReservation;
    Reservation testReservation1;
    Reservation testReservation2;
    Reservation testReservation3;
    Reservation testReservation4;
    Reservation test;


    Client testClient1;
    Client testClient2;
    Client testClient3;

//    String UserUUID1 = "6d4eaba3-7277-4364-9d77-0030ca3d9529";
//    String UserUUID2 = "1e83883c-4d8f-46ad-b6b9-19d125d5599f";
//    String UserUUID3 = "e7e364a9-0183-4f46-b8c0-7ab248adbc1d";
//
//    String CourtUUID1 = "7a3eda80-be8e-4ff6-a788-18da81387a87";

    LocalDateTime rightNow1 = LocalDateTime.now();

    Client client1;
    Client client2;
    Client client3;
    Client client4;

    final String testClientType = "normal";
    final String testClientPass = "Haslo1234!";
    Client testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testKlient", testClientPass, testClientType);


    Court testCourt = new Court(UUID.fromString("bd67f4f3-bddf-4ad8-b563-38e2c0b8d34e"), 10, 2, 1);
    Court court1;
    Court court2;
    Court court3;
    Court court4;




    @BeforeEach
    void init() {

        //testReservation = new Reservation(UUID.randomUUID(),testClient,testCourt, rightNow1);
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
        ReservationService reservationService = new ReservationService(addReservationPort,
                deleteReservationPort, getAllArchiveReservationsPort,
                getAllClientReservationsPort, getAllCurrentReservationsPort,
                getClientCurrentReservationsPort, getClientEndedReservationsPort,
                getCourtCurrentReservationPort, getCourtEndedReservationPort,
                getReservationByIdPort, returnCourtPort,
                getCourtByIdUseCase, modifyCourtUseCase);
        assertNotNull(reservationService);
    }

    @Test
    void testGetAllCurrentReservations() {
        when(getAllCurrentReservationsPort.getAllCurrentReservations()).thenReturn(List.of(testReservation1,testReservation2, testReservation3));

        List<Reservation> reservationList = reservationService.getAllCurrentReservations();
        assertEquals(reservationList.size(), 3);
        assertEquals(testReservation1, reservationList.get(0));
        assertEquals(testReservation2, reservationList.get(1));
        assertEquals(testReservation3, reservationList.get(2));
    }


    @Test
    void testGetAllArchiveReservations() {

        LocalDateTime date = LocalDateTime.of(2015,
                Month.JULY, 29, 19, 30, 40);



        Reservation reservation1 = new Reservation(UUID.randomUUID(),client1,court1, date);
        Reservation reservation2 = new Reservation(UUID.randomUUID(),client3,court1, date);
        Reservation reservation3 = new Reservation(UUID.randomUUID(),client2,court1, date);

        List<Reservation> expectedReservations = new ArrayList<>();
        expectedReservations.add(reservation1);
        expectedReservations.add(reservation2);
        expectedReservations.add(reservation3);

        when(getAllArchiveReservationsPort.getAllArchiveReservations()).thenReturn(expectedReservations);


        List<Reservation> actualReservations = reservationService.getAllArchiveReservations();

        assertEquals(expectedReservations, actualReservations);
         verify(getAllArchiveReservationsPort, times(1)).getAllArchiveReservations();
    }


    @Test
    void testAddReservationSuccess() {

        String clientId = UUID.randomUUID().toString();
        String courtId = UUID.randomUUID().toString();
        LocalDateTime beginTime = LocalDateTime.now();
        Reservation testReservation = new Reservation(UUID.randomUUID(), new Client(UUID.randomUUID(), "", "", "", "", ""), new Court(UUID.randomUUID(), 0, 0, 0), beginTime);
        Court testCourt = new Court(UUID.fromString(courtId), 0, 0, 0);

        when(addReservationPort.addReservation(any(Reservation.class))).thenReturn(testReservation);
        when(getCourtByIdUseCase.getCourtById(any(UUID.class))).thenReturn(testCourt);


        Reservation result = reservationService.addReservation(clientId, courtId, beginTime);


        assertNotNull(result);
        assertEquals(testReservation, result);
        assertTrue(testCourt.isRented());

        verify(addReservationPort, times(1)).addReservation(any(Reservation.class));
        verify(getCourtByIdUseCase, times(1)).getCourtById(UUID.fromString(courtId));
        verify(modifyCourtUseCase, times(1)).modifyCourt(testCourt);
    }

    @Test
    void testAddReservationFailureReservationException() {

        String clientId = UUID.randomUUID().toString();
        String courtId = UUID.randomUUID().toString();
        LocalDateTime beginTime = LocalDateTime.now();

        when(addReservationPort.addReservation(any(Reservation.class))).thenReturn(null);


        assertThrows(ReservationException.class, () -> reservationService.addReservation(clientId, courtId, beginTime));
    }

    @Test
    void testAddReservationFailureMyMongoException() {
        String clientId = UUID.randomUUID().toString();
        String courtId = UUID.randomUUID().toString();
        LocalDateTime beginTime = LocalDateTime.now();

        when(addReservationPort.addReservation(any(Reservation.class))).thenThrow(new MyMongoException("MongoDB Exception"));

        assertThrows(ReservationException.class, () -> reservationService.addReservation(clientId, courtId, beginTime));
    }



    @Test
    void testGetReservationByIdNull() {
        when(getReservationByIdPort.getReservationById(any())).thenReturn(null);

        assertNull(reservationService.getReservationById(UUID.randomUUID()));
    }

    @Test
    void testGetReservationById() {
        when(getAllCurrentReservationsPort.getAllCurrentReservations()).thenReturn(List.of(testReservation1,testReservation2, testReservation3));
        when(getReservationByIdPort.getReservationById(testReservation1.getId())).thenReturn(testReservation1);

        List<Reservation> reservationList = reservationService.getAllCurrentReservations();
        assertEquals(reservationList.size(), 3);

        assertEquals(testReservation1, reservationService.getReservationById(reservationList.get(0).getId()));
    }

    @Test
    void testGetReservationByIdWhenNull() {
        UUID randomReservationId = UUID.randomUUID();

        when(getAllCurrentReservationsPort.getAllCurrentReservations()).thenReturn(List.of(testReservation1, testReservation2, testReservation3));
        when(getReservationByIdPort.getReservationById(randomReservationId)).thenReturn(null);

        List<Reservation> reservationList = reservationService.getAllCurrentReservations();
        Reservation foundReservation = reservationService.getReservationById(randomReservationId);

        assertNull(foundReservation);
    }

    @Test
    void testGetAllClientReservations(){
        when(getAllClientReservationsPort.getAllClientReservations(testClient.getId())).thenReturn(List.of(testReservation3,testReservation4));

        List<Reservation> reservationList = reservationService.getAllClientReservations(testClient.getId());
        assertEquals(reservationList.size(), 2);
        assertEquals(testReservation3, reservationList.get(0));
        assertEquals(testReservation4, reservationList.get(1));

    }

    @Test
    void testGetClientEndedReservations() {

        UUID clientId = UUID.randomUUID();
        LocalDateTime rightNow1 = LocalDateTime.now();
        Reservation testReservation1 = new Reservation(UUID.randomUUID(), null, null, rightNow1);
        Reservation testReservation2 = new Reservation(UUID.randomUUID(), null, null, rightNow1);

        List<Reservation> expectedReservations = new ArrayList<>();
        expectedReservations.add(testReservation1);
        expectedReservations.add(testReservation2);

        when(getClientEndedReservationsPort.getClientEndedReservation(clientId)).thenReturn(expectedReservations);

        List<Reservation> actualReservations = reservationService.getClientEndedReservation(clientId);

        assertEquals(expectedReservations, actualReservations);
    }

    @Test
    void testGetClientEndedReservationsWhenNull() {

        UUID clientId = UUID.randomUUID();

        when(getClientEndedReservationsPort.getClientEndedReservation(clientId)).thenReturn(null);

        List<Reservation> actualReservations = reservationService.getClientEndedReservation(clientId);

        assertNull(actualReservations);
    }


    @Test
    void testGetAllClientReservationsWhenNull() {
        UUID clientId = UUID.randomUUID();
        when(getAllClientReservationsPort.getAllClientReservations(clientId)).thenReturn(null);
        List<Reservation> reservationList = reservationService.getAllClientReservations(clientId);

        assertNull(reservationList);
    }




    @Test
    void testAddReservationInvalidClientId() {
        String invalidClientId = "invalid_id";
        String courtId = UUID.randomUUID().toString();
        LocalDateTime beginTime = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(invalidClientId, courtId, beginTime));
        verifyNoInteractions(addReservationPort);
        verifyNoInteractions(getCourtByIdUseCase);
        verifyNoInteractions(modifyCourtUseCase);
    }

    @Test
    void testAddReservationInvalidCourtId() {

        String clientId = UUID.randomUUID().toString();
        String invalidCourtId = "invalid_id";
        LocalDateTime beginTime = LocalDateTime.now();


        assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(clientId, invalidCourtId, beginTime));
        verifyNoInteractions(addReservationPort);
        verifyNoInteractions(getCourtByIdUseCase);
        verifyNoInteractions(modifyCourtUseCase);
    }

    @Test
    void testGetCourtCurrentReservation() {

        UUID courtId = UUID.randomUUID();
        Reservation testReservation = new Reservation(UUID.randomUUID(), null, new Court(courtId, 0, 0, 0), LocalDateTime.now());

        when(getCourtCurrentReservationPort.getCourtCurrentReservation(courtId)).thenReturn(testReservation);


        Reservation reservation = reservationService.getCourtCurrentReservation(courtId);


        assertEquals(testReservation, reservation);
        verify(getCourtCurrentReservationPort, times(1)).getCourtCurrentReservation(courtId);
    }

    @Test
    void testGetCourtCurrentReservationWhenNull() {

        UUID courtId = UUID.randomUUID();
        when(getCourtCurrentReservationPort.getCourtCurrentReservation(courtId)).thenReturn(null);
        Reservation reservation = reservationService.getCourtCurrentReservation(courtId);

        assertNull(reservation);
    }

    @Test
    void testGetCourtEndedReservations() {

        UUID courtId = UUID.randomUUID();
        Reservation testReservation1 = new Reservation(UUID.randomUUID(), null, new Court(courtId, 0, 0, 0), LocalDateTime.now());
        Reservation testReservation2 = new Reservation(UUID.randomUUID(), null, new Court(courtId, 0, 0, 0), LocalDateTime.now());
        Reservation testReservation3 = new Reservation(UUID.randomUUID(), null, new Court(courtId, 0, 0, 0), LocalDateTime.now());

        when(getCourtEndedReservationPort.getCourtEndedReservation(courtId)).thenReturn(List.of(testReservation1, testReservation2, testReservation3));


        List<Reservation> reservations = reservationService.getCourtEndedReservation(courtId);


        assertEquals(3, reservations.size());
        assertEquals(testReservation1, reservations.get(0));
        assertEquals(testReservation2, reservations.get(1));
        assertEquals(testReservation3, reservations.get(2));
        verify(getCourtEndedReservationPort, times(1)).getCourtEndedReservation(courtId);
    }




    @Test
    void testDeleteReservations() {
        Mockito.doNothing().when(deleteReservationPort).deleteReservation(any(UUID.class));
        reservationService.deleteReservation(testReservation1.getId());
        verify(deleteReservationPort, times(1)).deleteReservation(testReservation1.getId());
    }

    @Test
    void testReturnCourt() {
        UUID courtId = UUID.randomUUID();
        Reservation testReservation = new Reservation(UUID.randomUUID(), testClient, new Court(courtId, 0, 0, 0), null);

        when(getCourtCurrentReservationPort.getCourtCurrentReservation(courtId)).thenReturn(testReservation);

        reservationService.returnCourt(courtId);
        verify(getCourtCurrentReservationPort, times(1)).getCourtCurrentReservation(courtId);
        verify(returnCourtPort, times(1)).returnCourt(testReservation);
        assertFalse(testReservation.getCourt().isRented());
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
