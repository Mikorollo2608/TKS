package tks.gv.rentservice.infrastructure.reservations.ports;

import tks.gv.rentservice.Reservation;

import java.util.UUID;

public interface GetCourtCurrentReservationPort {

    Reservation getCourtCurrentReservation(UUID courtId);
}
