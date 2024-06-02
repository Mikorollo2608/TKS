package tks.gv.rentservice.ui.reservations.ports;

import tks.gv.rentservice.Reservation;

import java.util.UUID;

public interface GetCourtCurrentReservationUseCase {

    Reservation getCourtCurrentReservation(UUID courtId);
}
