package tks.gv.ui.reservations.ports;

import java.util.UUID;

public interface CheckClientReservationBalanceUseCase {
    double checkClientReservationBalance(UUID clientId);
}
