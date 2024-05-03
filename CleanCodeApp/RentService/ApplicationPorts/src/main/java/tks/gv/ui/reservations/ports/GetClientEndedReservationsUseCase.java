package tks.gv.ui.reservations.ports;

import tks.gv.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetClientEndedReservationsUseCase {

    List<Reservation> getClientEndedReservation (UUID clientId);
}
