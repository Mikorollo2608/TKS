package tks.gv;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.ReservationException;
import tks.gv.infrastructure.reservations.ports.*;
import tks.gv.ui.courts.ports.GetCourtByIdUseCase;
import tks.gv.ui.courts.ports.ModifyCourtUseCase;
import tks.gv.ui.reservations.ports.*;

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

    @Override
    public double checkClientReservationBalance(UUID clientId) {
        double sum = 0.0;
        for (var res : getClientEndedReservation(clientId)) {
            sum += res.getReservationCost();
        }
        return sum;
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
        reservation.getCourt().setRented(false);
        returnCourtPort.returnCourt(reservation);
        modifyCourtUseCase.modifyCourt(reservation.getCourt());
    }


}
