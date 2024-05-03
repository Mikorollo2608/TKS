package tks.gv.userservice.controllers;

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


import tks.gv.userservice.data.dto.AdminDTO;
import tks.gv.userservice.data.dto.UserDTO;
import tks.gv.userservice.data.mappers.dto.AdminMapper;

import tks.gv.userservice.exceptions.UserLoginException;
import tks.gv.userservice.exceptions.UserException;

import tks.gv.userservice.userinterface.ports.admins.ChangeAdminStatusUseCase;
import tks.gv.userservice.userinterface.ports.admins.GetAllAdminsUseCase;
import tks.gv.userservice.userinterface.ports.admins.GetAdminByIdUseCase;
import tks.gv.userservice.userinterface.ports.admins.GetAdminByLoginUseCase;
import tks.gv.userservice.userinterface.ports.admins.ModifyAdminUseCase;
import tks.gv.userservice.userinterface.ports.admins.RegisterAdminUseCase;
import tks.gv.userservice.Admin;

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
    public ResponseEntity<String> addAdmin(@Validated({UserDTO.BasicUserValidation.class, UserDTO.PasswordValidation.class}) @RequestBody AdminDTO admin,
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
    public List<AdminDTO> getAllAdmins(HttpServletResponse response) {
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
    public AdminDTO getAdminById(@PathVariable("id") String id, HttpServletResponse response) {
        Admin admin = getAdminByIdUseCase.getAdminById(id);
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return AdminMapper.toUserDTO(admin);
    }

    @GetMapping("/get")
    public AdminDTO getAdminByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Admin admin = getAdminByLoginUseCase.getAdminByLogin(login);
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return AdminMapper.toUserDTO(admin);
    }

    @GetMapping("/match")
    public List<AdminDTO> getAdminByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
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
    public ResponseEntity<String> modifyAdmin(@Validated(UserDTO.BasicUserValidation.class) @RequestBody AdminDTO modifiedAdmin,
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
                    new AdminDTO(
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
}
