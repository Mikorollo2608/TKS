package tks.gv.rentservice.ui.reservations.ports;

import java.util.UUID;

public interface CheckClientReservationBalanceUseCase {
    double checkClientReservationBalance(UUID clientId);
}
