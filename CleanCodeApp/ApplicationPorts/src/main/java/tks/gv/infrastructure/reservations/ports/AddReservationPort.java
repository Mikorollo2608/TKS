package tks.gv.infrastructure.reservations.ports;

import tks.gv.reservations.Reservation;

import java.time.LocalDateTime;

public interface AddReservationPort {
    Reservation addReservation(Reservation reservation);
}
