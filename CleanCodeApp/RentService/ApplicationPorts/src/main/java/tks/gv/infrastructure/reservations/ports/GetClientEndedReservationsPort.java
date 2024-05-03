package tks.gv.infrastructure.reservations.ports;

import tks.gv.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetClientEndedReservationsPort {

    List<Reservation> getClientEndedReservation (UUID clientId);
}
