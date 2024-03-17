package tks.gv.controllers.users;

import jakarta.servlet.http.HttpServletRequest;
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

import tks.gv.data.dto.in.ClientDTORequest;
import tks.gv.data.dto.in.ClientRegisterDTORequest;
import tks.gv.data.dto.in.UserDTORequest.BasicUserValidation;
import tks.gv.data.dto.in.UserDTORequest.PasswordValidation;

import tks.gv.data.dto.out.ClientDTOResponse;
import tks.gv.data.mappers.dto.ClientMapper;
import tks.gv.exceptions.UserException;
import tks.gv.exceptions.UserLoginException;

import tks.gv.userinterface.users.ports.clients.ChangeClientStatusUseCase;
import tks.gv.userinterface.users.ports.clients.GetAllClientsUseCase;
import tks.gv.userinterface.users.ports.clients.GetClientByIdUseCase;
import tks.gv.userinterface.users.ports.clients.GetClientByLoginUseCase;
import tks.gv.userinterface.users.ports.clients.ModifyClientUseCase;
import tks.gv.userinterface.users.ports.clients.RegisterClientUseCase;
import tks.gv.users.Client;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final RegisterClientUseCase registerClientUseCase;
    private final GetAllClientsUseCase getAllClientsUseCase;
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final GetClientByLoginUseCase getClientByLoginUseCase;
    private final ModifyClientUseCase modifyClientUseCase;
    private final ChangeClientStatusUseCase changeClientStatusUseCase;

    @PostMapping(value = "/addClient", consumes = "application/json")
    public ResponseEntity<String> addClient(@Validated({BasicUserValidation.class, PasswordValidation.class}) @RequestBody ClientRegisterDTORequest client,
                                            Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream()
                            .map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            registerClientUseCase.registerClient(ClientMapper.fromUserDTO(client));
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ClientDTOResponse> getAllClients(HttpServletResponse response) {
        List<Client> resultList = getAllClientsUseCase.getAllClients();

        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(ClientMapper::toUserDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ClientDTOResponse getClientById(@PathVariable("id") String id, HttpServletResponse response) {
        Client client = getClientByIdUseCase.getClientById(id);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ClientMapper.toUserDTO(client);
    }

    @GetMapping("/get")
    public ClientDTOResponse getClientByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Client client = getClientByLoginUseCase.getClientByLogin(login);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ClientMapper.toUserDTO(client);
    }

    @GetMapping("/match")
    public List<ClientDTOResponse> getClientByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<Client> resultList = getClientByLoginUseCase.getClientByLoginMatching(login);
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(ClientMapper::toUserDTO)
                .toList();
    }

    @PutMapping("/modifyClient")
    public ResponseEntity<String> modifyClient(@Validated(BasicUserValidation.class) @RequestBody ClientDTORequest modifiedClient,
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
            modifyClientUseCase.modifyClient(ClientMapper.fromUserDTO(
                    new ClientDTORequest(
                            modifiedClient.getId(),
                            modifiedClient.getFirstName(),
                            modifiedClient.getLastName(),
                            modifiedClient.getLogin(),
                            null,
                            modifiedClient.isArchive(),
                            modifiedClient.getClientType())
            ));

        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/activate/{id}")
    public void activateClient(@PathVariable("id") String id, HttpServletResponse response) {
        changeClientStatusUseCase.activateClient(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveClient(@PathVariable("id") String id, HttpServletResponse response) {
        changeClientStatusUseCase.deactivateClient(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }


    ///TODO co z tym??
////    @PatchMapping("/changePassword/{id}")
////    public ResponseEntity<String> changeClientPassword(@PathVariable("id") String id,
////                                                       @Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
////                                                       Errors errors) {
////        if (errors.hasErrors()) {
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
////                    .body(errors.getAllErrors()
////                            .stream().map(ObjectError::getDefaultMessage)
////                            .toList()
////                            .toString()
////                    );
////        }
////
////        try {
////            clientService.changeClientPassword(id, body);
////        } catch (IllegalStateException ise) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
////        }
////
////        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
////    }


//    /*---------------------------------------FOR CLIENT-------------------------------------------------------------*/
////    @GetMapping("/get/me")
////    public ClientDTO getClientByLogin(HttpServletResponse response) {
////        ClientDTO client = clientService.getClientByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
////        if (client == null) {
////            response.setStatus(HttpStatus.NO_CONTENT.value());
////            return null;
////        }
////        String etag = jwsService.generateSignatureForClient(client);
////        response.setHeader(HttpHeaders.ETAG, etag);
////        return client;
////    }
//
//    @PutMapping("/modifyClient/me")
//    public ResponseEntity<String> modifyClient(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
//                                               @Validated(BasicUserValidation.class) @RequestBody ClientDTO modifiedClient,
//                                               Errors errors) {
//
//        if (ifMatch == null || ifMatch.isBlank()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Brak naglowka IF-MATCH!");
//        }
//
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
//            ClientDTO finalModifyClient = new ClientDTO(modifiedClient.getId(), modifiedClient.getFirstName(),
//                    modifiedClient.getLastName(), modifiedClient.getLogin(), null, modifiedClient.isArchive(),
//                    modifiedClient.getClientType());
////            if (jwsService.verifyClientSignature(ifMatch, finalModifyClient)) {
//            clientService.modifyClient(finalModifyClient);
////            } else {
////                throw new UserException("Proba zmiany niedozwolonego pola!");
////            }
//
//        } catch (UserLoginException ule) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
//        } catch (UserException ue) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//
////    @PatchMapping("/changePassword/me")
////    public ResponseEntity<String> changeClientPassword(@Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
////                                                       Errors errors) {
////        ClientDTO client = clientService.getClientByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
////        if (errors.hasErrors()) {
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
////                    .body(errors.getAllErrors()
////                            .stream().map(ObjectError::getDefaultMessage)
////                            .toList()
////                            .toString()
////                    );
////        }
////
////        try {
////            clientService.changeClientPassword(client.getId(), body);
////        } catch (IllegalStateException ise) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
////        }
////
////        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
////    }
}

