package tks.gv.rentservice.infrastructure.reservation.ports;

import tks.gv.rentservice.Reservation;

import java.util.UUID;

public interface GetCourtCurrentReservationPort {

    Reservation getCourtCurrentReservation(UUID courtId);
}
