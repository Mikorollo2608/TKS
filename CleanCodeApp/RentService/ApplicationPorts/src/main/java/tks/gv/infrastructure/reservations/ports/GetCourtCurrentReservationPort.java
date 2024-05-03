package tks.gv.infrastructure.reservations.ports;

import tks.gv.Reservation;

import java.util.UUID;

public interface GetCourtCurrentReservationPort {

    Reservation getCourtCurrentReservation(UUID courtId);
}
