package tks.gv.infrastructure.reservations.ports;

import java.util.UUID;
public interface DeleteReservationPort {
    void deleteReservation(UUID uuid);
}
