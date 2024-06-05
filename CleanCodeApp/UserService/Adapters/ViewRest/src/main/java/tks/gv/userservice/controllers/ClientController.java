package tks.gv.userservice.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
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

import tks.gv.userservice.UserServicePublisher;
import tks.gv.userservice.data.dto.ClientDTO;
import tks.gv.userservice.data.mappers.dto.ClientMapper;
import tks.gv.userservice.data.dto.UserDTO;


import tks.gv.userservice.exceptions.UserException;
import tks.gv.userservice.exceptions.UserLoginException;

import tks.gv.userservice.userinterface.ports.clients.ChangeClientStatusUseCase;
import tks.gv.userservice.userinterface.ports.clients.DeleteClientUseCase;
import tks.gv.userservice.userinterface.ports.clients.GetAllClientsUseCase;
import tks.gv.userservice.userinterface.ports.clients.GetClientByIdUseCase;
import tks.gv.userservice.userinterface.ports.clients.GetClientByLoginUseCase;
import tks.gv.userservice.userinterface.ports.clients.ModifyClientUseCase;
import tks.gv.userservice.userinterface.ports.clients.RegisterClientUseCase;
import tks.gv.userservice.Client;

import java.util.List;

@Slf4j
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
    private final DeleteClientUseCase deleteClientUseCase;

    private final UserServicePublisher publisher;

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
            Client createdClient = registerClientUseCase.registerClient(ClientMapper.fromUserDTO(client));
            if (createdClient != null) {
                publisher.sendCreate(createdClient);
            }
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ClientDTO> getAllClients(HttpServletResponse response) {
        log.warn(SecurityContextHolder.getContext().getAuthentication().getName());
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(System.out::println);

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
                            modifiedClient.isArchive())
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

    @DeleteMapping("/delete/{login}")
    public void deleteClient(@PathVariable("login") String login, HttpServletResponse response) {
        deleteClientUseCase.deleteClient(login);
        publisher.sendDelete(login);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}

