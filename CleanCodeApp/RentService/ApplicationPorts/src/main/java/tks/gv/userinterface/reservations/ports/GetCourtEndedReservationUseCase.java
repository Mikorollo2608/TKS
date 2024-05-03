package tks.gv.userinterface.reservations.ports;

import tks.gv.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetCourtEndedReservationUseCase {

    List<Reservation> getCourtEndedReservation (UUID courtId);
}
