package tks.gv.rentservice.infrastructure.reservation.ports;

import tks.gv.rentservice.Reservation;

public interface AddReservationPort {
    Reservation addReservation(Reservation reservation);
}
