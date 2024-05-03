package tks.gv.userinterface.reservations.ports;

import tks.gv.Reservation;

import java.time.LocalDateTime;

public interface AddReservationUseCase {
    Reservation addReservation(String clientId, String courtId, LocalDateTime beginTime);
}
