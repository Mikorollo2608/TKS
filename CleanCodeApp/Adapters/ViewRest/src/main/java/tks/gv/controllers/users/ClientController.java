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

import tks.gv.data.dto.ClientDTO;
import tks.gv.data.dto.UserDTO;


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

    @PostMapping(value = "/addClient")
    public ResponseEntity<String> addClient(@Validated({UserDTO.BasicUserValidation.class, UserDTO.PasswordValidation.class}) @RequestBody ClientDTO client,
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
    public List<ClientDTO> getAllClients(HttpServletResponse response) {
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
    public ClientDTO getClientById(@PathVariable("id") String id, HttpServletResponse response) {
        Client client = getClientByIdUseCase.getClientById(id);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ClientMapper.toUserDTO(client);
    }

    @GetMapping("/get")
    public ClientDTO getClientByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Client client = getClientByLoginUseCase.getClientByLogin(login);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ClientMapper.toUserDTO(client);
    }

    @GetMapping("/match")
    public List<ClientDTO> getClientByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
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
    public ResponseEntity<String> modifyClient(@Validated(UserDTO.BasicUserValidation.class) @RequestBody ClientDTO modifiedClient,
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
                    new ClientDTO(
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

}

