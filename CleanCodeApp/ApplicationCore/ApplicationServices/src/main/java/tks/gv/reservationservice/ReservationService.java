package tks.gv.reservationservice;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tks.gv.courts.Court;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.ReservationException;
import tks.gv.reservations.Reservation;
import tks.gv.infrastructure.reservations.ports.*;
import tks.gv.userinterface.courts.ports.GetCourtByIdUseCase;
import tks.gv.userinterface.courts.ports.ModifyCourtUseCase;
import tks.gv.userinterface.reservations.ports.*;
import tks.gv.users.Client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class ReservationService implements AddReservationUseCase, CheckClientReservationBalanceUseCase,
                                    DeleteReservationUseCase, GetAllArchiveReservationsUseCase,
                                    GetAllClientReservationsUseCase, GetAllCurrentReservationsUseCase,
                                    GetClientCurrentReservationsUseCase, GetClientEndedReservationsUseCase,
                                    GetCourtCurrentReservationUseCase, GetCourtEndedReservationUseCase,
                                    GetReservationByIdUseCase, ReturnCourtUseCase {

    private AddReservationPort addReservationPort;
    private DeleteReservationPort deleteReservationPort;
    private GetAllArchiveReservationsPort getAllArchiveReservationsPort;
    private GetAllClientReservationsPort getAllClientReservationsPort;
    private GetAllCurrentReservationsPort getAllCurrentReservationsPort;
    private GetClientCurrentReservationsPort getClientCurrentReservationsPort;
    private GetClientEndedReservationsPort getClientEndedReservationsPort;
    private GetCourtCurrentReservationPort getCourtCurrentReservationPort;
    private GetCourtEndedReservationPort getCourtEndedReservationPort;
    private GetReservationByIdPort getReservationByIdPort;
    private ReturnCourtPort returnCourtPort;
    private GetCourtByIdUseCase getCourtByIdUseCase;
    private ModifyCourtUseCase modifyCourtUseCase;


    @Autowired
    public ReservationService(AddReservationPort addReservationPort,
                              DeleteReservationPort deleteReservationPort, GetAllArchiveReservationsPort getAllArchiveReservationsPort,
                              GetAllClientReservationsPort getAllClientReservationsPort, GetAllCurrentReservationsPort getAllCurrentReservationsPort,
                              GetClientCurrentReservationsPort getClientCurrentReservationsPort, GetClientEndedReservationsPort getClientEndedReservationsPort,
                              GetCourtCurrentReservationPort getCourtCurrentReservationPort, GetCourtEndedReservationPort getCourtEndedReservationPort,
                              GetReservationByIdPort getReservationByIdPort, ReturnCourtPort returnCourtPort,
                              GetCourtByIdUseCase getCourtByIdUseCase, ModifyCourtUseCase modifyCourtUseCase) {

        this.addReservationPort = addReservationPort;
        this.deleteReservationPort = deleteReservationPort;
        this.getAllArchiveReservationsPort = getAllArchiveReservationsPort;
        this.getAllClientReservationsPort = getAllClientReservationsPort;
        this.getAllCurrentReservationsPort = getAllCurrentReservationsPort;
        this.getClientCurrentReservationsPort = getClientCurrentReservationsPort;
        this.getClientEndedReservationsPort = getClientEndedReservationsPort;
        this.getCourtCurrentReservationPort = getCourtCurrentReservationPort;
        this.getCourtEndedReservationPort = getCourtEndedReservationPort;
        this.getReservationByIdPort = getReservationByIdPort;
        this.returnCourtPort = returnCourtPort;
        this.getCourtByIdUseCase = getCourtByIdUseCase;
        this.modifyCourtUseCase = modifyCourtUseCase;
    }

    @Override
    public Reservation addReservation(String clientId, String courtId, LocalDateTime beginTime) {
        try {
            Reservation newReservation = addReservationPort.addReservation(
                    new Reservation(null,
                            new Client(UUID.fromString(clientId), "", "", "", "", ""),
                            new Court(UUID.fromString(courtId), 0, 0, 0),
                            beginTime));
            if (newReservation == null) {
                throw new ReservationException("Nie udalo sie utworzyc rezerwacji! - brak odpowiedzi");
            }
            Court court = getCourtByIdUseCase.getCourtById(UUID.fromString(courtId));
            court.setRented(true);
            modifyCourtUseCase.modifyCourt(court);
            return newReservation;
        } catch (MyMongoException exception) {
            throw new ReservationException("Nie udalo sie utworzyc rezerwacji - " + exception.getMessage());
        }
    }

    //TODO IMPLEMENTACJA MA BYĆ W TYM MIEJSCU
    @Override
    public double checkClientReservationBalance(UUID clientId) {
        return 0;//checkClientReservationBalancePort.checkClientReservationBalance(clientId);
    }

    @Override
    public void deleteReservation(UUID uuid) {
        deleteReservationPort.deleteReservation(uuid);
    }

    @Override
    public List<Reservation> getAllArchiveReservations() {
        return getAllArchiveReservationsPort.getAllArchiveReservations();
    }

    @Override
    public List<Reservation> getAllClientReservations(UUID clientId) {
        return getAllClientReservationsPort.getAllClientReservations(clientId);
    }

    @Override
    public List<Reservation> getAllCurrentReservations() {
        return getAllCurrentReservationsPort.getAllCurrentReservations();
    }

    @Override
    public List<Reservation> getClientCurrentReservations(UUID clientId) {
        return getClientCurrentReservationsPort.getClientCurrentReservations(clientId);
    }

    @Override
    public List<Reservation> getClientEndedReservation(UUID clientId) {
        return getClientEndedReservationsPort.getClientEndedReservation(clientId);
    }

    @Override
    public Reservation getCourtCurrentReservation(UUID courtId) {
        return getCourtCurrentReservationPort.getCourtCurrentReservation(courtId);
    }

    @Override
    public List<Reservation> getCourtEndedReservation(UUID courtId) {
        return getCourtEndedReservationPort.getCourtEndedReservation(courtId);
    }

    @Override
    public Reservation getReservationById(UUID uuid) {
        return getReservationByIdPort.getReservationById(uuid);
    }

    @Override
    public void returnCourt(UUID courtId) {
        Reservation reservation = getCourtCurrentReservation(courtId);
        reservation.endReservation(null);
        returnCourtPort.returnCourt(reservation);
        reservation.getCourt().setRented(false);
    }
}
//
//import com.mongodb.client.model.Filters;
//
//import lombok.NoArgsConstructor;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import tks.gv.data.repositories.ReservationMongoRepository;
//import tks.gv.model.logic.courts.Court;
//import tks.gv.model.logic.reservations.Reservation;
//import tks.gv.users.Client;
//import tks.gv.exceptions.MyMongoException;
//import tks.gv.exceptions.ReservationException;
//
//import tks.gv.restapi.data.dto.ReservationDTO;
//import tks.gv.restapi.data.mappers.ReservationMapper;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@NoArgsConstructor
//public class ReservationService {
//
//    private ReservationMongoRepository reservationRepository;
//
//    @Autowired
//    public ReservationService(ReservationMongoRepository reservationRepository) {
//        this.reservationRepository = reservationRepository;
//    }
//
//    public ReservationDTO makeReservation(String clientId, String courtId, LocalDateTime beginTime) {
//        try {
//            Reservation newReservation = reservationRepository.create(
//                    new Reservation(null,
//                            new Client(UUID.fromString(clientId), "", "", "", "", ""),
//                            new Court(UUID.fromString(courtId), 0, 0, 0),
//                            beginTime));
//            if (newReservation == null) {
//                throw new ReservationException("Nie udalo sie utworzyc rezerwacji! - brak odpowiedzi");
//            }
//            return ReservationMapper.toJsonReservation(newReservation);
//        } catch (MyMongoException exception) {
//            throw new ReservationException("Nie udalo sie utworzyc rezerwacji - " + exception.getMessage());
//        }
//    }
//
//    public ReservationDTO makeReservation(String clientId, String courtId) {
//        return makeReservation(clientId, courtId, LocalDateTime.now());
//    }
//
//    public void returnCourt(String courtId, LocalDateTime endTime) {
//        try {
//            reservationRepository.update(UUID.fromString(courtId), endTime);
//        } catch (MyMongoException exception) {
//            throw new ReservationException("Blad transakcji. - " + exception.getMessage());
//        }
//    }
//
//    public void returnCourt(String courtId) {
//        returnCourt(courtId, LocalDateTime.now());
//    }
//
//    public ReservationDTO getReservationById(String uuid) {
//        Reservation reservation = reservationRepository.readByUUID(UUID.fromString(uuid));
//        return reservation != null ? ReservationMapper.toJsonReservation(reservation) : null;
//    }
//
//    public List<ReservationDTO> getAllCurrentReservations() {
//        return reservationRepository.read(Filters.eq("endtime", null))
//                .stream().map(ReservationMapper::toJsonReservation)
//                .toList();
//    }
//
//    public List<ReservationDTO> getAllArchiveReservations() {
//        return reservationRepository.read(Filters.ne("endtime", null))
//                .stream().map(ReservationMapper::toJsonReservation)
//                .toList();
//    }
//
//    public List<ReservationDTO> getAllClientReservations(String clientId) {
//        return reservationRepository.read(Filters.eq("clientid", clientId))
//                .stream().map(ReservationMapper::toJsonReservation)
//                .toList();
//    }
//
//    public List<ReservationDTO> getClientCurrentReservations(String clientId) {
//        return reservationRepository.read(Filters.and(
//                        Filters.eq("clientid", clientId),
//                        Filters.eq("endtime", null)))
//                .stream().map(ReservationMapper::toJsonReservation)
//                .toList();
//    }
//
//    public List<ReservationDTO> getClientEndedReservations(String clientId) {
//        return reservationRepository.read(Filters.and(
//                        Filters.eq("clientid", clientId),
//                        Filters.ne("endtime", null)))
//                .stream().map(ReservationMapper::toJsonReservation)
//                .toList();
//    }
//
//    public ReservationDTO getCourtCurrentReservation(String courtId) {
//        var list = reservationRepository.read(
//                Filters.and(Filters.eq("courtid", courtId),
//                        Filters.eq("endtime", null)));
//        return !list.isEmpty() ? ReservationMapper.toJsonReservation(list.get(0)) : null;
//    }
//
//    public List<ReservationDTO> getCourtEndedReservation(String courtId) {
//        return reservationRepository.read(Filters.and(
//                        Filters.eq("courtid", courtId),
//                        Filters.ne("endtime", null)))
//                .stream().map(ReservationMapper::toJsonReservation)
//                .toList();
//    }
//
//    public void deleteReservation(String reservationId) {
//        try {
//            reservationRepository.delete(UUID.fromString(reservationId));
//        } catch (IllegalStateException e) {
//            throw new ReservationException("Nie mozna usunac zakonczonej rezerwacji");
//        } catch (Exception exception) {
//            throw new MyMongoException("Nie udalo sie usunac podanej rezerwacji. - " + exception.getMessage());
//        }
//    }
//
//    public double checkClientReservationBalance(String clientId) {
//        double sum = 0;
//        List<ReservationDTO> reservationList = getClientEndedReservations(clientId);
//        for (var reservation : reservationList) {
//            sum += reservation.getReservationCost();
//        }
//        return sum;
//    }
//
//
//    /*----------------------------------------------HANDLE UUID----------------------------------------------*/
//
//    public ReservationDTO makeReservation(UUID clientId, UUID courtId, LocalDateTime beginTime) {
//        return makeReservation(clientId.toString(), courtId.toString(), beginTime);
//    }
//
//    public ReservationDTO makeReservation(UUID clientId, UUID courtId) {
//        return makeReservation(clientId.toString(), courtId.toString(), LocalDateTime.now());
//    }
//
//    public void returnCourt(UUID courtId, LocalDateTime endTime) {
//        returnCourt(courtId.toString(), endTime);
//    }
//
//    public void returnCourt(UUID courtId) {
//        returnCourt(courtId.toString(), LocalDateTime.now());
//    }
//
//    public ReservationDTO getReservationById(UUID uuid) {
//        return getReservationById(uuid.toString());
//    }
//
//    public List<ReservationDTO> getAllClientReservations(UUID clientId) {
//        return getAllClientReservations(clientId.toString());
//    }
//
//    public List<ReservationDTO> getClientCurrentReservations(UUID clientId) {
//        return getClientCurrentReservations(clientId.toString());
//    }
//
//    public List<ReservationDTO> getClientEndedReservations(UUID clientId) {
//        return getClientEndedReservations(clientId.toString());
//    }
//
//    public ReservationDTO getCourtCurrentReservation(UUID courtId) {
//        return getCourtCurrentReservation(courtId.toString());
//    }
//
//    public List<ReservationDTO> getCourtEndedReservation(UUID courtId) {
//        return getCourtEndedReservation(courtId.toString());
//    }
//
//    public void deleteReservation(UUID uuid) {
//        deleteReservation(uuid.toString());
//    }
//
//    public double checkClientReservationBalance(UUID clientId) {
//        return checkClientReservationBalance(clientId.toString());
//    }
//}
