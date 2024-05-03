package tks.gv.userinterface.reservations.ports;

import tks.gv.Reservation;

import java.util.UUID;

public interface GetReservationByIdUseCase {
    Reservation getReservationById(UUID uuid);
}
