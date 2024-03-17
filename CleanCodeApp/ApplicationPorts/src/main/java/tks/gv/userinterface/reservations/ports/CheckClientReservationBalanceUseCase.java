package tks.gv.userinterface.reservations.ports;

import java.util.UUID;

public interface CheckClientReservationBalanceUseCase {
    double checkClientReservationBalance(UUID clientId);
}
