package tks.gv.infrastructure.courts.ports;

import java.util.UUID;

public interface DeleteCourtPort {
    void delteCourt(UUID id);
}
