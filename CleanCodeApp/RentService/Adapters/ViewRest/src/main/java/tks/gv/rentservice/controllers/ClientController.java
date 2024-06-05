package tks.gv.rentservice.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
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

import tks.gv.rentservice.data.dto.ClientDTO;

import tks.gv.rentservice.data.mappers.dto.ClientMapper;
import tks.gv.rentservice.exceptions.ClientException;
import tks.gv.rentservice.exceptions.ClientLoginException;

import tks.gv.rentservice.ui.clients.ports.ChangeClientStatusUseCase;
import tks.gv.rentservice.ui.clients.ports.GetAllClientsUseCase;
import tks.gv.rentservice.ui.clients.ports.GetClientByIdUseCase;
import tks.gv.rentservice.ui.clients.ports.GetClientByLoginUseCase;
import tks.gv.rentservice.ui.clients.ports.ModifyClientUseCase;
import tks.gv.rentservice.ui.clients.ports.RegisterClientUseCase;
import tks.gv.rentservice.Client;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ClientController {
    private final RegisterClientUseCase registerClientUseCase;
    private final GetAllClientsUseCase getAllClientsUseCase;
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final GetClientByLoginUseCase getClientByLoginUseCase;
    private final ModifyClientUseCase modifyClientUseCase;
    private final ChangeClientStatusUseCase changeClientStatusUseCase;

    @PostMapping(value = "/addClient")
    public ResponseEntity<String> addClient(@Validated({ClientDTO.BasicClientValidation.class}) @RequestBody ClientDTO client,
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
            registerClientUseCase.registerClient(ClientMapper.fromDTO(client));
        } catch (ClientLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (ClientException ue) {
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
                .map(ClientMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ClientDTO getClientById(@PathVariable("id") String id, HttpServletResponse response) {
        Client client = getClientByIdUseCase.getClientById(id);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ClientMapper.toDTO(client);
    }

    @GetMapping("/get")
    public ClientDTO getClientByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Client client = getClientByLoginUseCase.getClientByLogin(login);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return ClientMapper.toDTO(client);
    }

    @GetMapping("/match")
    public List<ClientDTO> getClientByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<Client> resultList = getClientByLoginUseCase.getClientByLoginMatching(login);
        if (resultList.isEmpty()) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }

        return resultList.stream()
                .map(ClientMapper::toDTO)
                .toList();
    }

    @PutMapping("/modifyClient")
    public ResponseEntity<String> modifyClient(@Validated(ClientDTO.BasicClientValidation.class) @RequestBody ClientDTO modifiedClient,
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
            modifyClientUseCase.modifyClient(ClientMapper.fromDTO(
                    new ClientDTO(
                            modifiedClient.getId(),
                            modifiedClient.getLogin(),
                            modifiedClient.isArchive(),
                            modifiedClient.getClientType())
            ));

        } catch (ClientLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (ClientException ue) {
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

