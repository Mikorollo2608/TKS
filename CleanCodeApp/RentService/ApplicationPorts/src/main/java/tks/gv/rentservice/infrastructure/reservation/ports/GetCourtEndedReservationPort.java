package tks.gv.rentservice.infrastructure.reservation.ports;

import tks.gv.rentservice.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetCourtEndedReservationPort {

    List<Reservation> getCourtEndedReservation (UUID courtId);
}
