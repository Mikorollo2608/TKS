package tks.gv.rentservice.ui.reservations.ports;

import tks.gv.rentservice.Reservation;

import java.util.List;

public interface GetAllCurrentReservationsUseCase {

    List<Reservation> getAllCurrentReservations();
}
