package tks.gv.userinterface.reservations.ports;

import java.util.UUID;

public interface ReturnCourtUseCase {

    void returnCourt (UUID courtId);
}
