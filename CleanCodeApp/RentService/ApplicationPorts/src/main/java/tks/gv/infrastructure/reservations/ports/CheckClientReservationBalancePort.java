package tks.gv.infrastructure.reservations.ports;

import java.util.UUID;

public interface CheckClientReservationBalancePort {
    double checkClientReservationBalance(UUID clientId);
}
