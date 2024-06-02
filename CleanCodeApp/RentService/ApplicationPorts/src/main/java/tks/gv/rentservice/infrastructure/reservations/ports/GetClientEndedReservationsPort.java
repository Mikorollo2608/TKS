package tks.gv.rentservice.infrastructure.reservations.ports;

import tks.gv.rentservice.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetClientEndedReservationsPort {

    List<Reservation> getClientEndedReservation (UUID clientId);
}
