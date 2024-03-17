package tks.gv.controllers.reservations;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tks.gv.data.mappers.dto.ReservationMapper;
import tks.gv.exceptions.MultiReservationException;
import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.ReservationException;

import tks.gv.reservations.Reservation;
import tks.gv.restapi.data.dto.ReservationDTO;

import tks.gv.userinterface.reservations.ports.AddReservationUseCase;
import tks.gv.userinterface.reservations.ports.CheckClientReservationBalanceUseCase;
import tks.gv.userinterface.reservations.ports.DeleteReservationUseCase;
import tks.gv.userinterface.reservations.ports.GetAllArchiveReservationsUseCase;
import tks.gv.userinterface.reservations.ports.GetAllClientReservationsUseCase;
import tks.gv.userinterface.reservations.ports.GetAllCurrentReservationsUseCase;
import tks.gv.userinterface.reservations.ports.GetClientCurrentReservationsUseCase;
import tks.gv.userinterface.reservations.ports.GetClientEndedReservationsUseCase;
import tks.gv.userinterface.reservations.ports.GetCourtCurrentReservationUseCase;
import tks.gv.userinterface.reservations.ports.GetCourtEndedReservationUseCase;
import tks.gv.userinterface.reservations.ports.GetReservationByIdUseCase;
import tks.gv.userinterface.reservations.ports.ReturnCourtUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final AddReservationUseCase addReservationUseCase;
    private final CheckClientReservationBalanceUseCase checkClientReservationBalanceUseCase;
    private final DeleteReservationUseCase deleteReservationUseCase;
    private final GetAllArchiveReservationsUseCase getAllArchiveReservationsUseCase;
    private final GetAllClientReservationsUseCase  getAllClientReservationsUseCase;
    private final GetAllCurrentReservationsUseCase getAllCurrentReservationsUseCase;
    private final GetClientCurrentReservationsUseCase getClientCurrentReservationsUseCase;
    private final GetClientEndedReservationsUseCase getClientEndedReservationsUseCase;
    private final GetCourtCurrentReservationUseCase  getCourtCurrentReservationUseCase;
    private final GetCourtEndedReservationUseCase getCourtEndedReservationUseCase;
    private final GetReservationByIdUseCase getReservationByIdUseCase;
    private final ReturnCourtUseCase returnCourtUseCase;

    @PostMapping("/addReservation")
    public ResponseEntity<String> addReservation(@RequestParam("clientId") String clientId, @RequestParam("courtId") String courtId,
                                                 @RequestParam(value = "date", required = false) String date) {
        try {
            if (date == null) {
                addReservationUseCase.addReservation(clientId, clientId);
            } else {
                addReservationUseCase.addReservation(clientId, courtId, LocalDateTime.parse(date));
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (MultiReservationException cne) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
        } catch (Exception ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ReservationDTO> getAllCurrentReservations(HttpServletResponse response) {
        List<Reservation> resultList = getAllCurrentReservationsUseCase.getAllCurrentReservations();
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return resultList.stream()
                .map(ReservationMapper::toJsonReservation)
                .toList();
    }

    @GetMapping("/archive")
    public List<ReservationDTO> getAllArchiveReservations(HttpServletResponse response) {
        List<Reservation> resultList = getAllArchiveReservationsUseCase.getAllArchiveReservations();
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return resultList.stream()
                .map(ReservationMapper::toJsonReservation)
                .toList();
    }

    @PostMapping("/returnCourt")
    public ResponseEntity<String> returnCourt(@RequestParam("courtId") String courtId, @RequestParam(value = "date", required = false) String date) {
        try {
            if (date == null) {
                returnCourtUseCase.returnCourt(UUID.fromString(courtId));
            } else {
                ///TODO dodac z data
//                returnCourtUseCase.returnCourt(UUID.fromString(courtId), LocalDateTime.parse(date));
            }
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        } catch (Exception ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ReservationDTO getReservationById(@PathVariable("id") String id, HttpServletResponse response) {
        Reservation reservation = getReservationByIdUseCase.getReservationById(UUID.fromString(id));
        if (reservation == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return ReservationMapper.toJsonReservation(reservation);
    }

    @GetMapping("/clientReservation")
    public List<ReservationDTO> getAllClientReservations(@RequestParam("clientId") String clientId, HttpServletResponse response) {
        List<Reservation> resultList = getAllClientReservationsUseCase.getAllClientReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return resultList.stream()
                .map(ReservationMapper::toJsonReservation)
                .toList();
    }

    @GetMapping("/clientReservation/current")
    public List<ReservationDTO> getClientCurrentReservations(@RequestParam("clientId") String clientId, HttpServletResponse response) {
        List<Reservation> resultList = getClientCurrentReservationsUseCase.getClientCurrentReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return resultList.stream()
                .map(ReservationMapper::toJsonReservation)
                .toList();
    }

    @GetMapping("/clientReservation/ended")
    public List<ReservationDTO> getClientEndedReservations(@RequestParam("clientId") String clientId, HttpServletResponse response) {
        List<Reservation> resultList = getClientEndedReservationsUseCase.getClientEndedReservation(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return resultList.stream()
                .map(ReservationMapper::toJsonReservation)
                .toList();
    }

    @GetMapping("/courtReservation/current")
    public ReservationDTO getCourtCurrentReservation(@RequestParam("courtId") String courtId, HttpServletResponse response) {
        Reservation reservation = getCourtCurrentReservationUseCase.getCourtCurrentReservation(UUID.fromString(courtId));
        if (reservation == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return ReservationMapper.toJsonReservation(reservation);
    }

    @GetMapping("/courtReservation/ended")
    public List<ReservationDTO> getCourtEndedReservation(@RequestParam("courtId") String courtId, HttpServletResponse response) {
        List<Reservation> resultList = getCourtEndedReservationUseCase.getCourtEndedReservation(UUID.fromString(courtId));
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        return resultList.stream()
                .map(ReservationMapper::toJsonReservation)
                .toList();
    }

    @GetMapping("/clientBalance")
    public double checkClientReservationBalance(@RequestParam("clientId") String clientId) {
        return checkClientReservationBalanceUseCase.checkClientReservationBalance(UUID.fromString(clientId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable("id") String id) {
        try {
            deleteReservationUseCase.deleteReservation(UUID.fromString(id));
        } catch (ReservationException re) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(re.getMessage());
        } catch (MyMongoException mme) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mme.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*---------------------------------------FOR CLIENT-------------------------------------------------------------*/
//    @PostMapping("/addReservation/me")
//    public ResponseEntity<String> addReservationByClient(@RequestParam("courtId") String courtId,
//                                                 @RequestParam(value = "date", required = false) String date) {
//        String clientId = clientService.getClientByLogin(
//                SecurityContextHolder.getContext().getAuthentication().getName()).getId();
//        return addReservation(clientId, courtId, date);
//    }
//
//    @GetMapping("/clientReservation/me")
//    public List<ReservationDTO> getAllClientReservationsByClient(HttpServletResponse response) {
//        String clientId = clientService.getClientByLogin(
//                SecurityContextHolder.getContext().getAuthentication().getName()).getId();
//        return getAllClientReservations(clientId, response);
//    }

//    @PostMapping("/returnCourt/me")
//    public ResponseEntity<String> returnCourtByClient(@RequestParam("courtId") String courtId, @RequestParam(value = "date", required = false) String date) {
//        String clientId = clientService.getClientByLogin(
//                SecurityContextHolder.getContext().getAuthentication().getName()).getId();
//
//        ReservationDTO reservation = reservationService.getCourtCurrentReservation(UUID.fromString(courtId));
//        if (reservation == null || !reservation.getClient().getId().equals(clientId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("To boisko nie jest wypozyczone przez aktualnego uzytkownika");
//        }
//
//        return returnCourt(courtId, date);
//    }
}