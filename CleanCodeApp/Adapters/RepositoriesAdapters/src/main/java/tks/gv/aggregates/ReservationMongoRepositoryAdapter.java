package tks.gv.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.infrastructure.reservations.ports.*;
import tks.gv.repositories.CourtMongoRepository;
import tks.gv.repositories.ReservationMongoRepository;
import tks.gv.repositories.UserMongoRepository;
import tks.gv.reservations.Reservation;

import java.util.List;
import java.util.UUID;

@Component
public class ReservationMongoRepositoryAdapter implements AddReservationPort, CheckClientReservationBalancePort,
        DeleteReservationPort, GetAllArchiveReservationsPort,
        GetAllClientReservationsPort, GetAllCurrentReservationsPort,
        GetClientCurrentReservationsPort, GetClientEndedReservationsPort,
        GetCourtCurrentReservationPort, GetCourtEndedReservationPort,
        GetReservationByIdPort, ReturnCourtPort{

    private final ReservationMongoRepository reservationMongoRepository;
    private final UserMongoRepository userMongoRepository;
    private final CourtMongoRepository courtMongoRepository;

    @Autowired
    public ReservationMongoRepositoryAdapter (ReservationMongoRepository reservationMongoRepository,
                                                UserMongoRepository userMongoRepository,
                                                CourtMongoRepository courtMongoRepository){

        this.reservationMongoRepository = reservationMongoRepository;
        this.userMongoRepository = userMongoRepository;
        this.courtMongoRepository = courtMongoRepository;
    }

    @Override
    public Reservation addReservation(Reservation reservation) {

    }

    @Override
    public double checkClientReservationBalance(UUID clientId) {
        return 0;
    }

    @Override
    public void deleteReservation(UUID uuid) {

    }

    @Override
    public List<Reservation> getAllArchiveReservations() {
        return null;
    }

    @Override
    public List<Reservation> getAllClientReservations(UUID clientId) {
        return null;
    }

    @Override
    public List<Reservation> getAllCurrentReservations(UUID clientId) {
        return null;
    }

    @Override
    public List<Reservation> getClientCurrentReservations(UUID clientId) {
        return null;
    }

    @Override
    public List<Reservation> getClientEndedReservation(UUID courtId) {
        return null;
    }

    @Override
    public Reservation getCourtCurrentReservation(UUID courtId) {
        return null;
    }

    @Override
    public Reservation getCourtEndedReservation(UUID courtId) {
        return null;
    }

    @Override
    public Reservation getReservationById(UUID uuid) {
        return null;
    }

    @Override
    public void returnCourt(UUID uuid) {

    }
}
