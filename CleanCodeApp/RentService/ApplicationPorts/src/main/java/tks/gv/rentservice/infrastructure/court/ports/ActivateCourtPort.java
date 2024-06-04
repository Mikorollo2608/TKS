package tks.gv.rentservice.infrastructure.court.ports;

import java.util.UUID;

public interface ActivateCourtPort {
    void activateCourt(UUID id);
}
