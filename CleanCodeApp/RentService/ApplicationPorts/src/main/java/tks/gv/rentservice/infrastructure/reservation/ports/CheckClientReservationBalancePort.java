package tks.gv.rentservice.infrastructure.reservation.ports;

import java.util.UUID;

public interface CheckClientReservationBalancePort {
    double checkClientReservationBalance(UUID clientId);
}
