package tks.gv.controllers.courts;

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

import tks.gv.data.dto.CourtDTO;
import tks.gv.data.mappers.dto.CourtMapper;
import tks.gv.exceptions.CourtException;
import tks.gv.exceptions.CourtNumberException;
import tks.gv.exceptions.MyMongoException;
import tks.gv.infrastructure.courts.ports.AddCourtPort;
import tks.gv.userinterface.courts.ports.CourtsUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courts")
public class CourtController {
    private final CourtsUseCase courtService;

    @Autowired
    public CourtController(CourtsUseCase courtService) {
        this.courtService = courtService;
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
            courtService.addCourt(CourtMapper.fromJsonCourt(court));
        } catch (CourtNumberException cne) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
        } catch (CourtException ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @GetMapping
//    public List<CourtDTO> getAllCourts(HttpServletResponse response) {
//        List<CourtDTO> resultList = courtService.getAllCourts();
//        if (resultList.isEmpty()) {
//            resultList = null;
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//        }
//        return resultList;
//    }
//
//    @GetMapping("/{id}")
//    public CourtDTO getCourtById(@PathVariable("id") String id, HttpServletResponse response) {
//        CourtDTO court = courtService.getCourtById(UUID.fromString(id));
//        if (court == null) {
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//        }
//        return court;
//    }
//
//    @GetMapping("/get")
//    public CourtDTO getCourtByCourtNumber(@RequestParam("number") String number, HttpServletResponse response) {
//        CourtDTO court = courtService.getCourtByCourtNumber(Integer.parseInt(number));
//        if (court == null) {
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//        }
//        return court;
//    }
//
//    @PutMapping("/modifyCourt/{id}")
//    public ResponseEntity<String> modifyCourt(@PathVariable("id") String id,
//                                              @Validated({CourtDTO.BasicCourtValidation.class}) @RequestBody CourtDTO modifiedCourt,
//                                              Errors errors) {
//        if (errors.hasErrors()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(errors.getAllErrors()
//                            .stream().map(ObjectError::getDefaultMessage)
//                            .toList()
//                            .toString()
//                    );
//        }
//
//        try {
//            CourtDTO finalModifyCourt = new CourtDTO(id, modifiedCourt.getArea(), modifiedCourt.getBaseCost(),
//                    modifiedCourt.getCourtNumber(), modifiedCourt.isArchive(), modifiedCourt.isRented());
//            courtService.modifyCourt(finalModifyCourt);
//        } catch (CourtNumberException cne) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
//        } catch (CourtException ce) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//
//    @PostMapping("/activate/{id}")
//    public void activateCourt(@PathVariable("id") String id, HttpServletResponse response) {
//        courtService.activateCourt(UUID.fromString(id));
//        response.setStatus(HttpStatus.NO_CONTENT.value());
//    }
//
//    @PostMapping("/deactivate/{id}")
//    public void archiveCourt(@PathVariable("id") String id, HttpServletResponse response) {
//        courtService.deactivateCourt(UUID.fromString(id));
//        response.setStatus(HttpStatus.NO_CONTENT.value());
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteCourt(@PathVariable("id") String id) {
//        try {
//            courtService.deleteCourt(UUID.fromString(id));
//        } catch (CourtException ce) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(ce.getMessage());
//        } catch (MyMongoException mme) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mme.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
