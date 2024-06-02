package tks.gv.rentservice.ui.courts.ports;

import java.util.UUID;

public interface DeactivateUseCase {
    void deactivateCourt(UUID id);
}
