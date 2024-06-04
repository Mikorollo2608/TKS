package tks.gv.rentservice.infrastructure.court.ports;

import java.util.UUID;

public interface DeactivateCourt {
    void deactivateCourt(UUID id);
}
