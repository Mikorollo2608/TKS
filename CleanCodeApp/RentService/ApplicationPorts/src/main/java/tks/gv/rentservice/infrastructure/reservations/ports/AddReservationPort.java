package tks.gv.rentservice.infrastructure.reservations.ports;

import tks.gv.rentservice.Reservation;

public interface AddReservationPort {
    Reservation addReservation(Reservation reservation);
}
