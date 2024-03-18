package adaptersTests;

import com.mongodb.client.model.Filters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.aggregates.ReservationMongoRepositoryAdapter;
import tks.gv.courts.Court;
import tks.gv.data.entities.ReservationEntity;
import tks.gv.data.mappers.entities.ReservationMapper;
import tks.gv.infrastructure.courts.ports.GetCourtByIdPort;
import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.repositories.ReservationMongoRepository;
import tks.gv.reservations.Reservation;
import tks.gv.users.Client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class ReservationMongoRepositoryAdapterTest {
    Client testClient;
    Client testClient2;
    Court court1;
    Court court2;
    List<Court> courtList;
    Reservation reservationBlank;
    Reservation reservationCurrent;
    Reservation reservationEnded;
    @Mock
    GetCourtByIdPort getCourtByIdPort;
    @Mock
    GetUserByIdPort getUserByIdPort;
    @Mock
    ReservationMongoRepository repository;
    @InjectMocks
    ReservationMongoRepositoryAdapter adapter;

    @BeforeEach
    void init() {
        court1 = new Court(UUID.fromString("bd67f4f3-bddf-4ad8-b563-38e2c0b8d34e"), 10, 2, 1);
        court2 = new Court(UUID.fromString("8ec379f1-3c16-40d6-abab-c43b47ca4f94"), 20, 4, 2);
        courtList = new ArrayList<>();
        courtList.add(court1);
        courtList.add(court2);
        testClient = new Client(UUID.randomUUID(), "Adam", "Niezgodka", "testLoginKlient", "Haslo1234!", "normal");
        testClient2 = new Client(UUID.randomUUID(), "NieAdam", "Zgodka", "testLoginKlient2", "Haslo1234!", "normal");
        reservationBlank = new Reservation(null,
                new Client(testClient2.getId(), "", "", "", "", ""),
                new Court(court2.getId(), 0, 0, 0),
                null);
        reservationCurrent = new Reservation(UUID.randomUUID(), testClient, court1, null);
        reservationEnded = new Reservation(UUID.randomUUID(), testClient, court2, LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        reservationEnded.endReservation(null);
    }

    @Test
    void testAddReservation() {
        Mockito.when(getCourtByIdPort.getCourtById(court2.getId())).thenReturn(court2);
        Mockito.when(getUserByIdPort.getUserById(testClient2.getId())).thenReturn(testClient2);

        ReservationEntity blankCreated = new ReservationEntity(UUID.randomUUID().toString(),
                reservationBlank.getClient().getId().toString(), reservationBlank.getCourt().getId().toString(), reservationBlank.getBeginTime(), null, 0);
        Mockito.when(repository.create(eq(ReservationMapper.toMongoReservation(reservationBlank)))).thenReturn(blankCreated);
        Reservation ret = adapter.addReservation(reservationBlank);
        assertEquals(reservationBlank.getBeginTime(), ret.getBeginTime());
        assertEquals(reservationBlank.getEndTime(), ret.getEndTime());
        assertEquals(reservationBlank.getClient().getId(), ret.getClient().getId());
        assertEquals(testClient2, ret.getClient());
        assertEquals(reservationBlank.getCourt().getId(), ret.getCourt().getId());
        assertEquals(court2, ret.getCourt());
    }

    @Test
    void testDeleteReservation(){
        Mockito.when(repository.delete(eq(reservationCurrent.getId()))).thenReturn(true);
        adapter.deleteReservation(reservationCurrent.getId());
        Mockito.verify(repository, Mockito.times(1)).delete(reservationCurrent.getId());
    }

    @Test
    void testGetAllArchiveReservations(){
        Mockito.when(getCourtByIdPort.getCourtById(court2.getId())).thenReturn(court2);
        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);
        Mockito.when(repository.read(eq(Filters.ne("endtime",null)))).thenReturn(List.of(ReservationMapper.toMongoReservation(reservationEnded)));
        assertEquals(reservationEnded, adapter.getAllArchiveReservations().get(0));
    }

    @Test
    void testGetAllCurrentReservations(){
        Mockito.when(getCourtByIdPort.getCourtById(court1.getId())).thenReturn(court1);
        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);
        Mockito.when(repository.read(eq(Filters.eq("endtime",null)))).thenReturn(List.of(ReservationMapper.toMongoReservation(reservationCurrent)));
        assertEquals(reservationCurrent, adapter.getAllCurrentReservations().get(0));
    }

    @Test
    void testGetAllClientReservations(){
        Mockito.when(getCourtByIdPort.getCourtById(court1.getId())).thenReturn(court1);
        Mockito.when(getCourtByIdPort.getCourtById(court2.getId())).thenReturn(court2);

        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        Mockito.when(repository.read(eq(Filters.eq("clientid",testClient.getId().toString()))))
                .thenReturn(
                        List.of(ReservationMapper.toMongoReservation(reservationCurrent),
                                ReservationMapper.toMongoReservation(reservationEnded)));
        List<Reservation> ret = adapter.getAllClientReservations(testClient.getId());
        assertEquals(reservationCurrent, ret.get(0));
        assertEquals(reservationEnded, ret.get(1));
    }

    @Test
    void testGetClientCurrentReservations(){
        Mockito.when(getCourtByIdPort.getCourtById(court1.getId())).thenReturn(court1);

        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        Mockito.when(repository.read(eq(Filters.and(Filters.eq("endtime", null), Filters.eq("clientid", testClient.getId().toString())))))
                .thenReturn(
                        List.of(ReservationMapper.toMongoReservation(reservationCurrent)));
        List<Reservation> ret = adapter.getClientCurrentReservations(testClient.getId());
        assertEquals(reservationCurrent, ret.get(0));
    }

    @Test
    void testGetClientEndedReservations(){
        Mockito.when(getCourtByIdPort.getCourtById(court2.getId())).thenReturn(court2);

        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        Mockito.when(repository.read(eq(Filters.and(Filters.ne("endtime", null), Filters.eq("clientid", testClient.getId().toString())))))
                .thenReturn(
                        List.of(ReservationMapper.toMongoReservation(reservationEnded)));
        List<Reservation> ret = adapter.getClientEndedReservation(testClient.getId());
        assertEquals(reservationEnded, ret.get(0));
    }

    @Test
    void testGetCourtCurrentReservations(){
        Mockito.when(getCourtByIdPort.getCourtById(court1.getId())).thenReturn(court1);

        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        Mockito.when(repository.read(eq(Filters.and(Filters.eq("endtime", null), Filters.eq("courtid", court1.getId().toString())))))
                .thenReturn(
                        List.of(ReservationMapper.toMongoReservation(reservationCurrent)));
        Reservation ret = adapter.getCourtCurrentReservation(court1.getId());
        assertEquals(reservationCurrent, ret);
    }

    @Test
    void testGetCourtEndedReservations(){
        Mockito.when(getCourtByIdPort.getCourtById(court2.getId())).thenReturn(court2);

        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        Mockito.when(repository.read(eq(Filters.and(Filters.ne("endtime", null), Filters.eq("courtid", court2.getId().toString())))))
                .thenReturn(
                        List.of(ReservationMapper.toMongoReservation(reservationEnded)));
        List<Reservation> ret = adapter.getCourtEndedReservation(court2.getId());
        assertEquals(reservationEnded, ret.get(0));
    }

    @Test
    void testGetReservationById(){
        Mockito.when(getCourtByIdPort.getCourtById(court1.getId())).thenReturn(court1);

        Mockito.when(getUserByIdPort.getUserById(testClient.getId())).thenReturn(testClient);

        Mockito.when(repository.readByUUID(eq(reservationCurrent.getId())))
                .thenReturn(ReservationMapper.toMongoReservation(reservationCurrent));
        assertEquals(reservationCurrent, adapter.getReservationById(reservationCurrent.getId()));
    }

    @Test
    void testReturnCourt(){
        Mockito.when(repository.updateByReplace(reservationEnded.getId(), ReservationMapper.toMongoReservation(reservationEnded)))
                .thenReturn(true);
        adapter.returnCourt(reservationEnded);
        Mockito.verify(repository, Mockito.times(1)).updateByReplace(reservationEnded.getId(), ReservationMapper.toMongoReservation(reservationEnded));
    }
}
