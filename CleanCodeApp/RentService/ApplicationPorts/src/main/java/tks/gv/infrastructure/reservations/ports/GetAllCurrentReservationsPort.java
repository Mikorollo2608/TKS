package tks.gv.infrastructure.reservations.ports;

import tks.gv.Reservation;

import java.util.List;

public interface GetAllCurrentReservationsPort {

    List<Reservation> getAllCurrentReservations();
}
