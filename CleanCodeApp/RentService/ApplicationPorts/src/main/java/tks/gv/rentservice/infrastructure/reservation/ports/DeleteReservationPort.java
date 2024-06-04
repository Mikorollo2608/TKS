package tks.gv.rentservice.infrastructure.reservation.ports;

import java.util.UUID;
public interface DeleteReservationPort {
    void deleteReservation(UUID uuid);
}
