//package tks.gv.security.controllers;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.Errors;
//import org.springframework.validation.ObjectError;
//import org.springframework.validation.annotation.Validated;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import tks.gv.model.exceptions.UserException;
//import tks.gv.model.exceptions.UserLoginException;
//import tks.gv.restapi.data.dto.UserDTO;
//import tks.gv.security.dto.AuthenticationRequest;
//import tks.gv.security.dto.ClientRegisterDTORequest;
//import tks.gv.security.dto.TokenResponse;
//import tks.gv.security.services.AuthenticationService;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthenticationController {
//
//    private final AuthenticationService service;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@Validated({UserDTO.BasicUserValidation.class, UserDTO.PasswordValidation.class})
//                                      @RequestBody ClientRegisterDTORequest request,
//                                      Errors errors) {
//        if (errors.hasErrors()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(errors.getAllErrors()
//                            .stream().map(ObjectError::getDefaultMessage)
//                            .toList()
//                            .toString()
//                    );
//        }
//
//        TokenResponse token;
//        try {
//            token = service.register(request);
//        } catch (UserLoginException ule) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
//        } catch (UserException ue) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
//        }
//
//        return ResponseEntity.ok(token);
//    }
//
//    @PostMapping("/authenticate")
//    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
//        TokenResponse token;
//        try {
//            token = service.authenticate(request);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//
//        return ResponseEntity.ok(token);
//    }
//
//    @PostMapping("/logout")
//    public void logout() {
//        SecurityContextHolder.clearContext();
//    }
//}
