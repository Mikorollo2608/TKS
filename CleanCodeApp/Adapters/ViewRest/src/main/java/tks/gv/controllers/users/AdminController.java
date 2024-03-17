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

import tks.gv.data.dto.in.AdminDTORequest;
import tks.gv.data.dto.in.UserDTORequest;
import tks.gv.data.mappers.dto.AdminMapper;

import tks.gv.exceptions.UserLoginException;
import tks.gv.exceptions.UserException;

import tks.gv.userinterface.users.ports.admins.ChangeAdminStatusUseCase;
import tks.gv.userinterface.users.ports.admins.GetAllAdminsUseCase;
import tks.gv.userinterface.users.ports.admins.GetAdminByIdUseCase;
import tks.gv.userinterface.users.ports.admins.GetAdminByLoginUseCase;
import tks.gv.userinterface.users.ports.admins.ModifyAdminUseCase;
import tks.gv.userinterface.users.ports.admins.RegisterAdminUseCase;
import tks.gv.users.Admin;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final RegisterAdminUseCase registerAdminUseCase;
    private final GetAllAdminsUseCase getAllAdminsUseCase;
    private final GetAdminByIdUseCase getAdminByIdUseCase;
    private final GetAdminByLoginUseCase getAdminByLoginUseCase;
    private final ModifyAdminUseCase modifyAdminUseCase;
    private final ChangeAdminStatusUseCase changeAdminStatusUseCase;
    
    @PostMapping("/addAdmin")
    public ResponseEntity<String> addAdmin(@Validated({UserDTORequest.BasicUserValidation.class, UserDTORequest.PasswordValidation.class}) @RequestBody AdminDTORequest admin,
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
            registerAdminUseCase.registerAdmin(AdminMapper.fromUserDTO(admin));
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<AdminDTORequest> getAllAdmins(HttpServletResponse response) {
        List<Admin> resultList = getAllAdminsUseCase.getAllAdmins();

        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(AdminMapper::toUserDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public AdminDTORequest getAdminById(@PathVariable("id") String id, HttpServletResponse response) {
        Admin admin = getAdminByIdUseCase.getAdminById(id);
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return AdminMapper.toUserDTO(admin);
    }

    @GetMapping("/get")
    public AdminDTORequest getAdminByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Admin admin = getAdminByLoginUseCase.getAdminByLogin(login);
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return AdminMapper.toUserDTO(admin);
    }

    @GetMapping("/match")
    public List<AdminDTORequest> getAdminByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<Admin> resultList = getAdminByLoginUseCase.getAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(AdminMapper::toUserDTO)
                .toList();
    }

    @PutMapping("/modifyAdmin")
    public ResponseEntity<String> modifyAdmin(@Validated(UserDTORequest.BasicUserValidation.class) @RequestBody AdminDTORequest modifiedAdmin,
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
            modifyAdminUseCase.modifyAdmin(AdminMapper.fromUserDTO(
                    new AdminDTORequest(
                            modifiedAdmin.getId(),
                            modifiedAdmin.getLogin(),
                            null,
                            modifiedAdmin.isArchive())
            ));

        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/activate/{id}")
    public void activateAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        changeAdminStatusUseCase.activateAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        changeAdminStatusUseCase.deactivateAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

//    @PatchMapping("/changePassword/{id}")
//    public ResponseEntity<String> changeAdminPassword(@PathVariable("id") String id,
//                                                      @Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
//                                                      Errors errors) {
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
//            adminService.changeAdminPassword(id, body);
//        } catch (IllegalStateException ise) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    /* me */
//    @GetMapping("/get/me")
//    public AdminDTO getAdminByLogin(HttpServletResponse response) {
//        AdminDTO admin = adminService.getAdminByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
//        if (admin == null) {
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//            return null;
//        }
//        String etag = "";
//        response.setHeader(HttpHeaders.ETAG, etag);
//        return admin;
//    }

//    @PatchMapping("/changePassword/me")
//    public ResponseEntity<String> changeResAdminPassword(@Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
//                                                         Errors errors) {
//        AdminDTO adminDTO = adminService.getAdminByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
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
//            adminService.changeAdminPassword(adminDTO.getId(), body);
//        } catch (IllegalStateException ise) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
