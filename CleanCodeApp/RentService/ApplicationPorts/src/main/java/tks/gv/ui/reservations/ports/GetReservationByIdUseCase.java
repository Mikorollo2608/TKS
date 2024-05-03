package tks.gv.ui.reservations.ports;

import tks.gv.Reservation;

import java.util.UUID;

public interface GetReservationByIdUseCase {
    Reservation getReservationById(UUID uuid);
}
