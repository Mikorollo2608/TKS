package tks.gv.infrastructure.reservations.ports;

import tks.gv.reservations.Reservation;
public interface AddReservationPort {
    Reservation addReservation(Reservation reservation);
}
