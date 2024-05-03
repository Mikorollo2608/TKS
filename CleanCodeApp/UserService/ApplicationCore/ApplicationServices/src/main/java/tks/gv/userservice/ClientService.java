package tks.gv.userservice;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tks.gv.userservice.exceptions.UnexpectedUserTypeException;
import tks.gv.userservice.exceptions.UserException;

import tks.gv.userservice.exceptions.UserReadServiceException;
import tks.gv.userservice.infrastructure.ports.AddUserPort;
import tks.gv.userservice.infrastructure.ports.ChangeUserStatusPort;
import tks.gv.userservice.infrastructure.ports.GetAllUsersPort;

import tks.gv.userservice.infrastructure.ports.GetUserByIdPort;
import tks.gv.userservice.infrastructure.ports.GetUserByLoginPort;
import tks.gv.userservice.infrastructure.ports.ModifyUserPort;

import tks.gv.userservice.userinterface.ports.clients.ChangeClientStatusUseCase;
import tks.gv.userservice.userinterface.ports.clients.GetAllClientsUseCase;
import tks.gv.userservice.userinterface.ports.clients.GetClientByIdUseCase;
import tks.gv.userservice.userinterface.ports.clients.GetClientByLoginUseCase;
import tks.gv.userservice.userinterface.ports.clients.ModifyClientUseCase;
import tks.gv.userservice.userinterface.ports.clients.RegisterClientUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class ClientService implements
        RegisterClientUseCase,
        GetAllClientsUseCase,
        GetClientByIdUseCase,
        GetClientByLoginUseCase,
        ModifyClientUseCase,
        ChangeClientStatusUseCase {

    private AddUserPort addUserPort;
    private GetAllUsersPort getAllUsersPort;
    private GetUserByIdPort getUserByIdPort;
    private GetUserByLoginPort getUserByLoginPort;
    private ModifyUserPort modifyUserPort;
    private ChangeUserStatusPort changeUserStatusPort;

    @Autowired
    public ClientService(AddUserPort addUserPort, GetAllUsersPort getAllUsersPort, GetUserByIdPort getUserByIdPort,
                         GetUserByLoginPort getUserByLoginPort, ModifyUserPort modifyUserPort,
                         ChangeUserStatusPort changeUserStatusPort) {
        this.addUserPort = addUserPort;
        this.getAllUsersPort = getAllUsersPort;
        this.getUserByIdPort = getUserByIdPort;
        this.getUserByLoginPort = getUserByLoginPort;
        this.modifyUserPort = modifyUserPort;
        this.changeUserStatusPort = changeUserStatusPort;
    }

    @Override
    public Client registerClient(Client client) {
        try {
            return userProjection(addUserPort.addUser(client));
        } catch (UnexpectedUserTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - " + exception.getMessage());
        }
    }

    @Override
    public Client getClientById(UUID clientId) {
        try {
            return userProjection(getUserByIdPort.getUserById(clientId));
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu klienta z bazy! - " + e.getMessage());
        }
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();
        for (var user : getAllUsersPort.getAllUsers()) {
            if (user instanceof Client client) {
                list.add(client);
            }
        }

        return list;
    }

    @Override
    public Client getClientByLogin(String login) {
        try {
            return userProjection(getUserByLoginPort.getUserByLogin(login));
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu klienta z bazy! - " + e.getMessage());
        }
    }

    @Override
    public List<Client> getClientByLoginMatching(String login) {
        try {
            List<Client> list = new ArrayList<>();
            for (var user : getUserByLoginPort.getUserByLoginMatching(login)) {
                if (user instanceof Client client) {
                    list.add(client);
                }
            }
            return list;
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu klienta z bazy! - " + e.getMessage());
        }
    }

    @Override
    public void modifyClient(Client modifiedClient) {
        modifyUserPort.modifyUser(modifiedClient);
    }

    @Override
    public void activateClient(UUID clientId) {
        changeUserStatusPort.activateUser(clientId);
    }

    @Override
    public void deactivateClient(UUID clientId) {
        changeUserStatusPort.deactivateUser(clientId);
    }

    private Client userProjection(User user) {
        if (user instanceof Client client) {
            return client;
        }
        return null;
    }
}
