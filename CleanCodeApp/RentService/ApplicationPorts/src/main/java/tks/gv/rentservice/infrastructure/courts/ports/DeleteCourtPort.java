package tks.gv.rentservice.infrastructure.courts.ports;

import java.util.UUID;

public interface DeleteCourtPort {
    void delteCourt(UUID id);
}
