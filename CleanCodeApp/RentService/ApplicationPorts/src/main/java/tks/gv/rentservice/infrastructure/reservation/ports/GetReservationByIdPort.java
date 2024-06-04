package tks.gv.rentservice.infrastructure.reservation.ports;
import java.util.UUID;
import tks.gv.rentservice.Reservation;
public interface GetReservationByIdPort {
    Reservation getReservationById(UUID uuid);

}
