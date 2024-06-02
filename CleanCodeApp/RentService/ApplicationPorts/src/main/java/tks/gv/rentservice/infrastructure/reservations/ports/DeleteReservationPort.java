package tks.gv.rentservice.infrastructure.reservations.ports;

import java.util.UUID;
public interface DeleteReservationPort {
    void deleteReservation(UUID uuid);
}
