package tks.gv.rentservice.aggregates;

import com.mongodb.client.model.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.rentservice.data.mappers.entities.ReservationMapper;
import tks.gv.rentservice.infrastructure.court.ports.GetCourtByIdPort;
import tks.gv.rentservice.infrastructure.client.ports.GetClientByIdPort;
import tks.gv.rentservice.infrastructure.reservation.ports.AddReservationPort;
import tks.gv.rentservice.infrastructure.reservation.ports.DeleteReservationPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetAllArchiveReservationsPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetAllClientReservationsPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetAllCurrentReservationsPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetClientCurrentReservationsPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetClientEndedReservationsPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetCourtCurrentReservationPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetCourtEndedReservationPort;
import tks.gv.rentservice.infrastructure.reservation.ports.GetReservationByIdPort;
import tks.gv.rentservice.infrastructure.reservation.ports.ReturnCourtPort;
import tks.gv.rentservice.repositories.ReservationMongoRepository;
import tks.gv.rentservice.Reservation;

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
    private final GetClientByIdPort getClientByIdPort;

    private Reservation alaReservationBuidler(Reservation reservation){
        if (reservation == null) return null;
        Reservation retReservation = new Reservation(reservation.getId(),
                getClientByIdPort.getClientById(reservation.getClient().getId()),
                getCourtByIdPort.getCourtById(reservation.getCourt().getId()),
                reservation.getBeginTime(),
                reservation.getEndTime(),
                reservation.getReservationCost());

        return retReservation;
    }

    @Autowired
    public ReservationMongoRepositoryAdapter(ReservationMongoRepository reservationMongoRepository,
                                             GetClientByIdPort getClientByIdPort, GetCourtByIdPort getCourtByIdPort) {
        this.getCourtByIdPort = getCourtByIdPort;
        this.getClientByIdPort = getClientByIdPort;
        this.reservationMongoRepository = reservationMongoRepository;
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        return alaReservationBuidler(ReservationMapper.fromReservationEntity(reservationMongoRepository.create(ReservationMapper.toReservationEntity(reservation))));
    }

    @Override
    public void deleteReservation(UUID uuid) {
        reservationMongoRepository.delete(uuid);
    }

    @Override
    public List<Reservation> getAllArchiveReservations() {
        return reservationMongoRepository.read(Filters.ne("endtime",null)).stream()
                .map(ReservationMapper::fromReservationEntity)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getAllClientReservations(UUID clientId) {
        return reservationMongoRepository.read(Filters.eq("clientid",clientId.toString())).stream()
                .map(ReservationMapper::fromReservationEntity)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getAllCurrentReservations() {
        return reservationMongoRepository.read(Filters.eq("endtime",null)).stream()
                .map(ReservationMapper::fromReservationEntity)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getClientCurrentReservations(UUID clientId) {
        return reservationMongoRepository.read(
                Filters.and(Filters.eq("endtime", null), Filters.eq("clientid", clientId.toString())))
                .stream()
                .map(ReservationMapper::fromReservationEntity)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public List<Reservation> getClientEndedReservation(UUID clientId) {
        return reservationMongoRepository.read(
                        Filters.and(Filters.ne("endtime", null), Filters.eq("clientid", clientId.toString())))
                .stream()
                .map(ReservationMapper::fromReservationEntity)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public Reservation getCourtCurrentReservation(UUID courtId) {
        var list = reservationMongoRepository.read(
                        Filters.and(Filters.eq("endtime", null), Filters.eq("courtid", courtId.toString())));
        return list.isEmpty() ? null : alaReservationBuidler(ReservationMapper.fromReservationEntity(list.get(0)));
    }

    @Override
    public List<Reservation> getCourtEndedReservation(UUID courtId) {
        return reservationMongoRepository.read(
                        Filters.and(Filters.ne("endtime", null), Filters.eq("courtid", courtId.toString())))
                .stream()
                .map(ReservationMapper::fromReservationEntity)
                .map(this::alaReservationBuidler).toList();
    }

    @Override
    public Reservation getReservationById(UUID uuid) {
        return alaReservationBuidler(ReservationMapper.fromReservationEntity(reservationMongoRepository.readByUUID(uuid)));
    }

    @Override
    public void returnCourt(Reservation reservation) {
        reservationMongoRepository.updateByReplace(reservation.getId(), ReservationMapper.toReservationEntity(reservation));
    }
}
