package tks.gv.rentservice.ui.reservations.ports;

import tks.gv.rentservice.Reservation;

import java.time.LocalDateTime;

public interface AddReservationUseCase {
    Reservation addReservation(String clientId, String courtId, LocalDateTime beginTime);
}
