package tks.gv.rentservice.ui.reservations.ports;

import java.util.UUID;

public interface ReturnCourtUseCase {

    void returnCourt (UUID courtId);
}
