package tks.gv.infrastructure.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.UUID;

public interface GetCourtCurrentReservationPort {

    Reservation getCourtCurrentReservation(UUID courtId);
}
