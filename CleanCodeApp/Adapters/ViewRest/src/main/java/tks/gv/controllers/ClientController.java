package tks.gv.restapi.controllers.users;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tks.gv.data.dto.ClientDTO;
import tks.gv.data.dto.UserDTO.BasicUserValidation;
import tks.gv.data.dto.UserDTO.PasswordValidation;

import tks.gv.data.mappers.dto.ClientMapper;
import tks.gv.exceptions.UserException;
import tks.gv.exceptions.UserLoginException;

import tks.gv.userinterface.users.ports.ClientsUseCase;
import tks.gv.users.Client;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientsUseCase clientService;
//    private final JwsService jwsService;

    @Autowired
    public ClientController(ClientsUseCase clientService
//            , JwsService jwsService
    ) {
        this.clientService = clientService;
//        this.jwsService = jwsService;
    }

    @PostMapping("/addClient")
    public ResponseEntity<String> addClient(@Validated({BasicUserValidation.class, PasswordValidation.class}) @RequestBody ClientDTO client,
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
            clientService.registerClient(ClientMapper.fromUserDTO(client));
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ClientDTO> getAllClients(HttpServletResponse response) {
        List<Client> resultList = clientService.getAllClients();

        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(ClientMapper::toUserDTO)
                .toList();
    }

//    @GetMapping("/{id}")
//    public ClientDTO getClientById(@PathVariable("id") String id, HttpServletResponse response) {
//        ClientDTO client = clientService.getClientById(id);
//        if (client == null) {
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//        }
//        return client;
//    }
//
//    @GetMapping("/get")
//    public ClientDTO getClientByLogin(@RequestParam("login") String login, HttpServletResponse response) {
//        ClientDTO client = clientService.getClientByLogin(login);
//        if (client == null) {
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//        }
//        return client;
//    }
//
//    @GetMapping("/match")
//    public List<ClientDTO> getClientByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
//        List<ClientDTO> resultList = clientService.getClientByLoginMatching(login);
//        if (resultList.isEmpty()) {
//            resultList = null;
//            response.setStatus(HttpStatus.NO_CONTENT.value());
//        }
//        return resultList;
//    }
//
//    @PutMapping("/modifyClient")
//    public ResponseEntity<String> modifyClient(HttpServletRequest httpServletRequest,
//                                               @Validated(BasicUserValidation.class) @RequestBody ClientDTO modifiedClient,
//                                               Errors errors) {
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
//
//            clientService.modifyClient(finalModifyClient);
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
//    @PostMapping("/activate/{id}")
//    public void activateClient(@PathVariable("id") String id, HttpServletResponse response) {
//        clientService.activateClient(id);
//        response.setStatus(HttpStatus.NO_CONTENT.value());
//    }
//
//    @PostMapping("/deactivate/{id}")
//    public void archiveClient(@PathVariable("id") String id, HttpServletResponse response) {
//        clientService.deactivateClient(id);
//        response.setStatus(HttpStatus.NO_CONTENT.value());
//    }
//
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
//
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

