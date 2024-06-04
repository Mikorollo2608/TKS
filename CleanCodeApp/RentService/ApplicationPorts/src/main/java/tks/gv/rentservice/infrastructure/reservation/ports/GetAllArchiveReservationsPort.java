package tks.gv.rentservice.infrastructure.reservation.ports;

import tks.gv.rentservice.Reservation;

import java.util.List;
public interface GetAllArchiveReservationsPort {

    List<Reservation> getAllArchiveReservations();
}
