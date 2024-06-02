package tks.gv.rentservice.ui.reservations.ports;

import tks.gv.rentservice.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetClientCurrentReservationsUseCase {

    List<Reservation> getClientCurrentReservations (UUID clientId);
}
