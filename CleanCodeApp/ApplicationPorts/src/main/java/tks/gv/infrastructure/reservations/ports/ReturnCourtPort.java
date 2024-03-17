package tks.gv.infrastructure.reservations.ports;

import java.util.UUID;
public interface ReturnCourtPort {
    void returnCourt(UUID uuid);
}
