package tks.gv.infrastructure.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.UUID;

public interface GetCourtEndedReservationPort {

    Reservation getCourtEndedReservation (UUID courtId);
}
