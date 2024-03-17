package tks.gv.infrastructure.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetCourtEndedReservationPort {

    List<Reservation> getCourtEndedReservation (UUID courtId);
}
