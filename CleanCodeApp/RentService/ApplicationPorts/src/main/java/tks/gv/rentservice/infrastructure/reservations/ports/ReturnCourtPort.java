package tks.gv.rentservice.infrastructure.reservations.ports;

import tks.gv.rentservice.Reservation;

public interface ReturnCourtPort {
    void returnCourt(Reservation reservation);
}
