package tks.gv.userinterface.reservations.ports;

import tks.gv.reservations.Reservation;
public interface AddReservationUseCase {
    Reservation addReservation(Reservation reservation);
}
