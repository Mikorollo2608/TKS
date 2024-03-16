package tks.gv.userservice;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tks.gv.exceptions.UnexpectedUserTypeException;
import tks.gv.exceptions.UserException;

import tks.gv.exceptions.UserReadServiceException;
import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.ChangeUserStatusPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.infrastructure.users.ports.GetUserByLoginPort;
import tks.gv.infrastructure.users.ports.ModifyUserPort;

import tks.gv.userinterface.users.ports.clients.ChangeClientStatusUseCase;
import tks.gv.userinterface.users.ports.clients.GetAllClientsUseCase;
import tks.gv.userinterface.users.ports.clients.GetClientByIdUseCase;
import tks.gv.userinterface.users.ports.clients.GetClientByLoginUseCase;
import tks.gv.userinterface.users.ports.clients.ModifyClientUseCase;
import tks.gv.userinterface.users.ports.clients.RegisterClientUseCase;

import tks.gv.users.Client;
import tks.gv.users.User;

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

////    public void changeClientPassword(String id, ChangePasswordDTORequest changePasswordDTO) {
////        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
////
////        if (!passwordEncoder.matches(changePasswordDTO.getActualPassword(), user.getPassword())) {
////            throw new IllegalStateException("Niepoprawne aktualne haslo!");
////        }
////        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmationPassword())) {
////            throw new IllegalStateException("Podane hasla roznia sie!");
////        }
////
////        userRepository.update(UUID.fromString(id), "password",
////                passwordEncoder.encode(changePasswordDTO.getNewPassword()));
////    }
//
//
//    /*----------------------------------------------HANDLE STRING----------------------------------------------*/

    public Client getClientById(String clientId) {
        return getClientById(UUID.fromString(clientId));
    }

    public void activateClient(String clientId) {
        activateClient(UUID.fromString(clientId));
    }

    public void deactivateClient(String clientId) {
        deactivateClient(UUID.fromString(clientId));
    }

////    public void changeClientPassword(UUID id, ChangePasswordDTORequest changePasswordDTO) {
////        changeClientPassword(id.toString(), changePasswordDTO);
////    }

    private Client userProjection(User user) {
        return user != null ? (Client) user : null;
    }
}
