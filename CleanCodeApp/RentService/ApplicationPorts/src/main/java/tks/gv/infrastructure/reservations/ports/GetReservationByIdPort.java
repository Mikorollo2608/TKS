package tks.gv.infrastructure.reservations.ports;
import java.util.UUID;
import tks.gv.Reservation;
public interface GetReservationByIdPort {
    Reservation getReservationById(UUID uuid);

}
