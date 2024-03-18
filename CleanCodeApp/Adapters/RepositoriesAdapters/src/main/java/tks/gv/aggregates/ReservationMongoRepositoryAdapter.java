package tks.gv.aggregates;

import com.mongodb.client.model.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.data.mappers.entities.ReservationMapper;
import tks.gv.infrastructure.courts.ports.GetCourtByIdPort;
import tks.gv.infrastructure.reservations.ports.*;
import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.repositories.ReservationMongoRepository;
import tks.gv.reservations.Reservation;
import tks.gv.users.Client;

import java.util.List;
import java.util.UUID;

@Component
public class ReservationMongoRepositoryAdapter implements AddReservationPort,
        DeleteReservationPort, GetAllArchiveReservationsPort,
        GetAllClientReservationsPort, GetAllCurrentReservationsPort,
        GetClientCurrentReservationsPort, GetClientEndedReservationsPort,
        GetCourtCurrentReservationPort, GetCourtEndedReservationPort,
        GetReservationByIdPort, ReturnCourtPort {

    private final ReservationMongoRepository reservationMongoRepository;
    private final GetCourtByIdPort getCourtByIdPort;
    private final GetUserByIdPort getUserByIdPort;

    private Reservation alaReservationBuidler(Reservation reservation){
        if (reservation == null) return null;
        Reservation retReservation = new Reservation(reservation.getId(),
                (Client)getUserByIdPort.getUserById(reservation.getClient().getId()),
                getCourtByIdPort.getCourtById(reservation.getCourt().getId()),
                reservation.getBeginTime(),
                reservation.getEndTime(),
                reservation.getReservationCost());

        return retReservation;
    }

    @Autowired
    public ReservationMongoRepositoryAdapter(ReservationMongoRepository reservationMongoRepository,
                                             GetUserByIdPort getUserByIdPort, GetCourtByIdPort getCourtByIdPort) {
        this.getCourtByIdPort = getCourtByIdPort;
        this.getUserByIdPort = getUserByIdPort;
        this.reservationMongoRepository = reservationMongoRepository;
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        return alaReservationBuidler(ReservationMapper.fromMongoReservation(reservationMongoRepository.create(ReservationMapper.toMongoReservation(reservation))));
    }

    @Override
    public void deleteReservation(UUID uuid) {
        reservationMongoRepository.delete(uuid);
    }

    @Override
    public List<Reservation> getAllArchiveReservations() {
        return reservationMongoRepository.read(Filters.ne("endtime",null)).stream()
                .map(ReservationMapper::fromMongoReservation)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getAllClientReservations(UUID clientId) {
        return reservationMongoRepository.read(Filters.eq("clientid",clientId.toString())).stream()
                .map(ReservationMapper::fromMongoReservation)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getAllCurrentReservations() {
        return reservationMongoRepository.read(Filters.eq("endtime",null)).stream()
                .map(ReservationMapper::fromMongoReservation)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getClientCurrentReservations(UUID clientId) {
        return reservationMongoRepository.read(
                Filters.and(Filters.eq("endtime", null), Filters.eq("clientid", clientId.toString())))
                .stream()
                .map(ReservationMapper::fromMongoReservation)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getClientEndedReservation(UUID clientId) {
        return reservationMongoRepository.read(
                        Filters.and(Filters.ne("endtime", null), Filters.eq("clientid", clientId.toString())))
                .stream()
                .map(ReservationMapper::fromMongoReservation)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public Reservation getCourtCurrentReservation(UUID courtId) {
        var list = reservationMongoRepository.read(
                        Filters.and(Filters.eq("endtime", null), Filters.eq("courtid", courtId.toString())));
        return list.isEmpty() ? null : alaReservationBuidler(ReservationMapper.fromMongoReservation(list.get(0)));
    }

    @Override
    public List<Reservation> getCourtEndedReservation(UUID courtId) {
        return reservationMongoRepository.read(
                        Filters.and(Filters.ne("endtime", null), Filters.eq("courtid", courtId.toString())))
                .stream()
                .map(ReservationMapper::fromMongoReservation)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public Reservation getReservationById(UUID uuid) {
        return alaReservationBuidler(ReservationMapper.fromMongoReservation(reservationMongoRepository.readByUUID(uuid)));
    }

    @Override
    public void returnCourt(Reservation reservation) {
        reservationMongoRepository.updateByReplace(reservation.getId(), ReservationMapper.toMongoReservation(reservation));
    }
}
