package tks.gv.userinterface.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetAllClientReservationsUseCase {
    List<Reservation> getAllClientReservations(UUID clientId);
}
