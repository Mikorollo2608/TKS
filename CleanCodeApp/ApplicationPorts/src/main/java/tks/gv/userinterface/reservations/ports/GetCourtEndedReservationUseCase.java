package tks.gv.userinterface.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.UUID;

public interface GetCourtEndedReservationUseCase {

    Reservation getCourtEndedReservation (UUID courtId);
}
