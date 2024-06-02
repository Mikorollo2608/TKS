package tks.gv.rentservice.infrastructure.reservations.ports;

import tks.gv.rentservice.Reservation;

import java.util.List;
public interface GetAllArchiveReservationsPort {

    List<Reservation> getAllArchiveReservations();
}
