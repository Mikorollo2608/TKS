package tks.gv.userinterface.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.List;

public interface GetAllArchiveReservationsUseCase {
    List<Reservation> getAllArchiveReservations();
}
