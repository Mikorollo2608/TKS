package tks.gv.ui.reservations.ports;

import tks.gv.Reservation;

import java.util.List;

public interface GetAllCurrentReservationsUseCase {

    List<Reservation> getAllCurrentReservations();
}
