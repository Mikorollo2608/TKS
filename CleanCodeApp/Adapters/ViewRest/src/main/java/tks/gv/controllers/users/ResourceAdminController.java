package tks.gv.controllers.users;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tks.gv.data.dto.in.ResourceAdminDTORequest;
import tks.gv.data.dto.in.UserDTORequest;
import tks.gv.data.mappers.dto.ResourceAdminMapper;

import tks.gv.exceptions.UserException;
import tks.gv.exceptions.UserLoginException;

import tks.gv.userinterface.users.ports.resourceadmins.ChangeResourceAdminStatusUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.GetAllResourceAdminsUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.GetResourceAdminByIdUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.GetResourceAdminByLoginUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.ModifyResourceAdminUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.RegisterResourceAdminUseCase;

import tks.gv.users.ResourceAdmin;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/resAdmins")
@RequiredArgsConstructor
public class ResourceAdminController {

    private final RegisterResourceAdminUseCase registerResourceAdminUseCase;
    private final GetAllResourceAdminsUseCase getAllResourceAdminsUseCase;
    private final GetResourceAdminByIdUseCase getResourceAdminByIdUseCase;
    private final GetResourceAdminByLoginUseCase getResourceAdminByLoginUseCase;
    private final ModifyResourceAdminUseCase modifyResourceAdminUseCase;
    private final ChangeResourceAdminStatusUseCase changeResourceAdminStatusUseCase;

    @PostMapping("/addResAdmin")
    public ResponseEntity<String> addResAdmin(@Validated({UserDTORequest.BasicUserValidation.class, UserDTORequest.PasswordValidation.class}) @RequestBody ResourceAdminDTORequest resourceAdmin,
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
            registerResourceAdminUseCase.registerResourceAdmin(ResourceAdminMapper.fromUserDTO(resourceAdmin));
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ResourceAdminDTORequest> getAllResAdmins(HttpServletResponse response) {
        List<ResourceAdmin> resultList = getAllResourceAdminsUseCase.getAllResourceAdmins();

        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(ResourceAdminMapper::toUserDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResourceAdminDTORequest getResAdminById(@PathVariable("id") String id, HttpServletResponse response) {
        ResourceAdmin resourceAdmin = getResourceAdminByIdUseCase.getResourceAdminById(UUID.fromString(id));
        if (resourceAdmin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ResourceAdminMapper.toUserDTO(resourceAdmin);
    }

    @GetMapping("/get")
    public ResourceAdminDTORequest getResAdminByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        ResourceAdmin resourceAdmin = getResourceAdminByLoginUseCase.getResourceAdminByLogin(login);
        if (resourceAdmin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ResourceAdminMapper.toUserDTO(resourceAdmin);
    }

    @GetMapping("/match")
    public List<ResourceAdminDTORequest> getResAdminByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<ResourceAdmin> resultList = getResourceAdminByLoginUseCase.getResourceAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(ResourceAdminMapper::toUserDTO)
                .toList();
    }

    @PutMapping("/modifyResAdmin")
    public ResponseEntity<String> modifyResAdmin(@Validated(UserDTORequest.BasicUserValidation.class) @RequestBody ResourceAdminDTORequest modifyResourceAdmin,
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
            modifyResourceAdminUseCase.modifyResourceAdmin(ResourceAdminMapper.fromUserDTO(
                    new ResourceAdminDTORequest(
                            modifyResourceAdmin.getId(),
                            modifyResourceAdmin.getLogin(),
                            null,
                            modifyResourceAdmin.isArchive())
            ));
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/activate/{id}")
    public void activateResAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        changeResourceAdminStatusUseCase.activateResourceAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveResAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        changeResourceAdminStatusUseCase.deactivateResourceAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

//    @PatchMapping("/changePassword/{id}")
//    public ResponseEntity<String> changeResAdminPassword(@PathVariable("id") String id,
//                                                         @Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
//                                                         Errors errors) {
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
//            resourceAdminService.changeResourceAdminPassword(id, body);
//        } catch (IllegalStateException ise) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    /* me */
//    @GetMapping("/get/me")
//    public ResourceAdminDTO getResAdminByLogin(HttpServletResponse response) {
//        ResourceAdminDTO resAdmin = resourceAdminService.getResourceAdminByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
//        if (resAdmin == null) {
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//            return null;
//        }
//        String etag = "";
//        response.setHeader(HttpHeaders.ETAG, etag);
//        return resAdmin;
//    }

//    @PatchMapping("/changePassword/me")
//    public ResponseEntity<String> changeResAdminPassword(@Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
//                                                       Errors errors) {
//        ResourceAdminDTO resourceAdminDTO = resourceAdminService.getResourceAdminByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
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
//            resourceAdminService.changeResourceAdminPassword(resourceAdminDTO.getId(), body);
//        } catch (Exception ise ) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
