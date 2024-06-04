package tks.gv.rentservice.infrastructure.reservation.ports;

import tks.gv.rentservice.Reservation;

public interface ReturnCourtPort {
    void returnCourt(Reservation reservation);
}
