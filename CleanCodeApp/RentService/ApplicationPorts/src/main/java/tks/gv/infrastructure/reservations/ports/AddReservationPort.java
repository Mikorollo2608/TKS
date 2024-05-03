package tks.gv.infrastructure.reservations.ports;

import tks.gv.Reservation;

public interface AddReservationPort {
    Reservation addReservation(Reservation reservation);
}
