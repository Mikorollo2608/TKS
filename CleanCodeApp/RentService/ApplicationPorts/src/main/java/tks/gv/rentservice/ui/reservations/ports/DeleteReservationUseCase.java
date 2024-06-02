package tks.gv.rentservice.ui.reservations.ports;

import java.util.UUID;

public interface DeleteReservationUseCase {
    void deleteReservation(UUID uuid);
}
