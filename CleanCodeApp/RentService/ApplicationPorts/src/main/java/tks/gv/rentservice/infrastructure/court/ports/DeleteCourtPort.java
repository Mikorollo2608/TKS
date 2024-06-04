package tks.gv.rentservice.infrastructure.court.ports;

import java.util.UUID;

public interface DeleteCourtPort {
    void delteCourt(UUID id);
}
