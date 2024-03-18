package tks.gv.infrastructure.reservations.ports;

import tks.gv.reservations.Reservation;

import java.util.List;

public interface GetAllCurrentReservationsPort {

    List<Reservation> getAllCurrentReservations();
}
