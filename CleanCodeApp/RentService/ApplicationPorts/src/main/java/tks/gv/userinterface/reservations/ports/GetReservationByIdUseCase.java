package tks.gv.userinterface.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.UUID;

public interface GetReservationByIdUseCase {
    Reservation getReservationById(UUID uuid);
}
