package mappersTests;

import tks.gv.Court;
import tks.gv.data.entities.CourtEntity;
import tks.gv.data.entities.ReservationEntity;
import tks.gv.data.mappers.entities.CourtMapper;
import tks.gv.data.mappers.entities.ReservationMapper;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.Reservation;
import tks.gv.Client;
import tks.gv.data.entities.ClientEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReservationMapperTest {
    UUID uuid = UUID.randomUUID();
    Client testClient;
    ClientEntity testClientMapper;
    Court testCourt;
    CourtEntity testCourtMapper;

    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    @BeforeEach
    void setUp() {
        testClient = new Client(UUID.randomUUID(), "John", "Smith", "12345678901", "12345678901","normal");
        testClientMapper = ClientMapper.toEntity(testClient);

        testCourt = new Court(UUID.randomUUID(), 1000, 100, 1);
        testCourtMapper = CourtMapper.toMongoCourt(testCourt);

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingMapper() {
        ReservationEntity reservationMapper = new ReservationEntity(uuid.toString(),
                testClient.getId().toString(), testCourt.getId().toString(), testTimeStart, testTimeEnd,
                200);
        assertNotNull(reservationMapper);

        assertEquals(uuid, UUID.fromString(reservationMapper.getId()));
        assertEquals(testClient.getId(), UUID.fromString(reservationMapper.getClientId()));
        assertEquals(testCourt.getId(), UUID.fromString(reservationMapper.getCourtId()));
        assertEquals(testTimeStart, reservationMapper.getBeginTime());
        assertEquals(testTimeEnd, reservationMapper.getEndTime());
        assertEquals(200, reservationMapper.getReservationCost());
    }

    @Test
    void testToMongoClientMethod() {
        Reservation reservation = new Reservation(UUID.randomUUID(), testClient, testCourt, testTimeStart);
        assertNotNull(reservation);

        ReservationEntity reservationMapper = ReservationMapper.toReservationEntity(reservation);
        assertNotNull(reservationMapper);

        assertEquals(reservation.getId(), UUID.fromString(reservationMapper.getId()));
        assertEquals(testClient.getId(), UUID.fromString(reservationMapper.getClientId()));
        assertEquals(testCourt.getId(), UUID.fromString(reservationMapper.getCourtId()));
        assertEquals(testTimeStart, reservationMapper.getBeginTime());
        assertNull(reservationMapper.getEndTime());
        assertEquals(0, reservationMapper.getReservationCost());

        Reservation reservationEnded = new Reservation(UUID.randomUUID(), testClient, testCourt, testTimeStart);
        assertNotNull(reservationEnded);
        reservationEnded.endReservation(testTimeEnd);

        ReservationEntity reservationMapperEnded = ReservationMapper.toReservationEntity(reservationEnded);
        assertNotNull(reservationMapperEnded);

        assertEquals(reservationEnded.getId(), UUID.fromString(reservationMapperEnded.getId()));
        assertEquals(testClient.getId(), UUID.fromString(reservationMapperEnded.getClientId()));
        assertEquals(testCourt.getId(), UUID.fromString(reservationMapperEnded.getCourtId()));
        assertEquals(testTimeStart, reservationMapperEnded.getBeginTime());
        assertEquals(testTimeEnd, reservationMapperEnded.getEndTime());
        assertEquals(300, reservationMapperEnded.getReservationCost());
    }

    @Test
    void testFromMongoClientMethod() {
        ReservationEntity reservationMapper = new ReservationEntity(uuid.toString(),
                testClient.getId().toString(), testCourt.getId().toString(), testTimeStart, null,
                0);
        assertNotNull(reservationMapper);

        Reservation reservation = ReservationMapper.fromReservationEntity(reservationMapper);
        assertNotNull(reservation);

        assertEquals(uuid, reservation.getId());
        assertEquals(testClient.getId(), reservation.getClient().getId());
        assertEquals(testCourt.getId(), reservation.getCourt().getId());
        assertEquals(testTimeStart, reservation.getBeginTime());
        assertNull(reservation.getEndTime());
        assertEquals(0, reservation.getReservationCost());


        ReservationEntity reservationMapperEnded = new ReservationEntity(uuid.toString(),
                testClient.getId().toString(), testCourt.getId().toString(), testTimeStart, testTimeEnd,
                300);
        assertNotNull(reservationMapperEnded);

        Reservation reservationEnded = ReservationMapper.fromReservationEntity(reservationMapperEnded);
        assertNotNull(reservationEnded);

        assertEquals(UUID.fromString(reservationMapperEnded.getId()), reservationEnded.getId());
        assertEquals(testClient.getId(), reservationEnded.getClient().getId());
        assertEquals(testCourt.getId(), reservationEnded.getCourt().getId());
        assertEquals(testTimeStart, reservationEnded.getBeginTime());
        assertEquals(testTimeEnd, reservationEnded.getEndTime());
        assertEquals(300, reservationEnded.getReservationCost());
    }
}
