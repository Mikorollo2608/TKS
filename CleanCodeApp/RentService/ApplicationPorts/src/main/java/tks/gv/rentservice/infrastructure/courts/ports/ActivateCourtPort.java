package tks.gv.rentservice.infrastructure.courts.ports;

import java.util.UUID;

public interface ActivateCourtPort {
    void activateCourt(UUID id);
}
