package tks.gv.rentservice.infrastructure.courts.ports;

import java.util.UUID;

public interface DeactivateCourt {
    void deactivateCourt(UUID id);
}
