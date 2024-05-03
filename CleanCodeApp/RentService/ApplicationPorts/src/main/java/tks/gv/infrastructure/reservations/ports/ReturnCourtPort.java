package tks.gv.infrastructure.reservations.ports;

import tks.gv.Reservation;

public interface ReturnCourtPort {
    void returnCourt(Reservation reservation);
}
