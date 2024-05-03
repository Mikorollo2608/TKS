package tks.gv.infrastructure.reservations.ports;

import tks.gv.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetAllClientReservationsPort {

    List<Reservation> getAllClientReservations(UUID clientId);
}
