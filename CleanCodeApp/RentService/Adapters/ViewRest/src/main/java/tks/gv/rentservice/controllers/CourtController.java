package tks.gv.rentservice.controllers;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tks.gv.rentservice.data.dto.CourtDTO;
import tks.gv.rentservice.data.mappers.dto.CourtMapper;
import tks.gv.rentservice.exceptions.CourtException;
import tks.gv.rentservice.exceptions.CourtNumberException;
import tks.gv.rentservice.exceptions.MyMongoException;
import tks.gv.rentservice.ui.courts.ports.ActivateCourtUseCase;
import tks.gv.rentservice.ui.courts.ports.AddCourtUseCase;
import tks.gv.rentservice.ui.courts.ports.DeactivateUseCase;
import tks.gv.rentservice.ui.courts.ports.DeleteCourtUseCase;
import tks.gv.rentservice.ui.courts.ports.GetAllCourtsUseCase;
import tks.gv.rentservice.ui.courts.ports.GetCourtByCourtNumberUseCase;
import tks.gv.rentservice.ui.courts.ports.GetCourtByIdUseCase;
import tks.gv.rentservice.ui.courts.ports.ModifyCourtUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courts")
public class CourtController {
    private final ActivateCourtUseCase activateCourt;
    private final AddCourtUseCase addCourt;
    private final DeactivateUseCase deactivateCourt;
    private final DeleteCourtUseCase deleteCourt;
    private final GetAllCourtsUseCase getAllCourts;
    private final GetCourtByIdUseCase getCourtById;
    private final GetCourtByCourtNumberUseCase getCourtByCourtNumber;
    private final ModifyCourtUseCase modifyCourt;

    @Autowired
    public CourtController(ActivateCourtUseCase activateCourt, AddCourtUseCase addCourt,
                           DeactivateUseCase deactivateCourt, DeleteCourtUseCase deleteCourt,
                           GetAllCourtsUseCase getAllCourts, GetCourtByIdUseCase getCourtById,
                           GetCourtByCourtNumberUseCase getCourtByCourtNumber,
                           ModifyCourtUseCase modifyCourt) {
        this.activateCourt = activateCourt;
        this.addCourt = addCourt;
        this.deactivateCourt = deactivateCourt;
        this.deleteCourt = deleteCourt;
        this.getAllCourts = getAllCourts;
        this.getCourtById = getCourtById;
        this.getCourtByCourtNumber = getCourtByCourtNumber;
        this.modifyCourt = modifyCourt;
    }

    @PostMapping("/addCourt")
    public ResponseEntity<String> addCourt(@Validated({CourtDTO.BasicCourtValidation.class}) @RequestBody CourtDTO court,
                                           Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            addCourt.addCourt(CourtMapper.fromJsonCourt(court));
        } catch (CourtNumberException cne) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
        } catch (CourtException ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<CourtDTO> getAllCourts(HttpServletResponse response) {
        List<CourtDTO> resultList = getAllCourts.getAllCourts().stream().map(CourtMapper::toJsonCourt).toList();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/{id}")
    public CourtDTO getCourtById(@PathVariable("id") String id, HttpServletResponse response) {
        CourtDTO court = CourtMapper.toJsonCourt(getCourtById.getCourtById(UUID.fromString(id)));
        if (court == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return court;
    }

    @GetMapping("/get")
    public CourtDTO getCourtByCourtNumber(@RequestParam("number") String number, HttpServletResponse response) {
        CourtDTO court = CourtMapper.toJsonCourt(getCourtByCourtNumber.getCourtByCourtNumber(Integer.parseInt(number)));
        if (court == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return court;
    }

    @PutMapping("/modifyCourt/{id}")
    public ResponseEntity<String> modifyCourt(@PathVariable("id") String id,
                                              @Validated({CourtDTO.BasicCourtValidation.class}) @RequestBody CourtDTO modifiedCourt,
                                              Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            CourtDTO finalModifyCourt = new CourtDTO(id, modifiedCourt.getArea(), modifiedCourt.getBaseCost(),
                    modifiedCourt.getCourtNumber(), modifiedCourt.isArchive(), modifiedCourt.isRented());
            modifyCourt.modifyCourt(CourtMapper.fromJsonCourt(finalModifyCourt));
        } catch (CourtNumberException cne) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
        } catch (CourtException ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/activate/{id}")
    public void activateCourt(@PathVariable("id") String id, HttpServletResponse response) {
        activateCourt.activateCourt(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveCourt(@PathVariable("id") String id, HttpServletResponse response) {
        deactivateCourt.deactivateCourt(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCourt(@PathVariable("id") String id) {
        try {
            deleteCourt.deleteCourt(UUID.fromString(id));
        } catch (CourtException ce) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ce.getMessage());
        } catch (MyMongoException mme) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mme.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
