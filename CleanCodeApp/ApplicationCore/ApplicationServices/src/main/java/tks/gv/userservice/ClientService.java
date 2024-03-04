package tks.gv.userservice;

import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tks.gv.exceptions.UserException;
import tks.gv.exceptions.MyMongoException;

import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.userinterface.users.ports.ClientsUseCase;

import tks.gv.users.Client;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class ClientService implements ClientsUseCase {

    private AddUserPort addUserPort;
    private GetAllUsersPort getAllUsersPort;

    @Autowired
    public ClientService(AddUserPort addUserPort, GetAllUsersPort getAllUsersPort) {
        this.addUserPort = addUserPort;
        this.getAllUsersPort = getAllUsersPort;
    }

    public void registerClient(Client client) {
        try {
            addUserPort.addUser(client);
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - " + exception.getMessage());
        }
    }

//    public ClientDTO getClientById(String clientID) {
//        User client = userRepository.readByUUID(UUID.fromString(clientID), Client.class);
//        return client != null ? ClientMapper.toJsonUser((Client) client) : null;
//    }

    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();
        for (var user : getAllUsersPort.getAllUsers()) {
            if (user instanceof Client client) {
                list.add(client);
            }
        }
        return list;
    }

//    public ClientDTO getClientByLogin(String login) {
//        var list = userRepository.read(Filters.eq("login", login), Client.class);
//        if (list.isEmpty() || (list.get(0) instanceof ResourceAdmin || list.get(0) instanceof Admin)) {
//            return null;
//        }
//        return ClientMapper.toJsonUser((Client) list.get(0));
//    }
//
//    public List<ClientDTO> getClientByLoginMatching(String login) {
//        List<ClientDTO> list = new ArrayList<>();
//        for (var user : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
//                Filters.eq("_clazz", "client")), Client.class)) {
//            list.add(ClientMapper.toJsonUser((Client) user));
//        }
//        return list;
//    }
//
//    public void modifyClient(ClientDTO modifiedClient) {
//        var list = userRepository.read(Filters.and(
//                Filters.eq("login", modifiedClient.getLogin()),
//                Filters.ne("_id", modifiedClient.getId())), Client.class);
//        if (!list.isEmpty()) {
//            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego klienta - " +
//                    "proba zmiany loginu na login wystepujacy juz u innego klienta");
//        }
//
//        if (!userRepository.updateByReplace(UUID.fromString(modifiedClient.getId()),
//                ClientMapper.fromJsonUser(modifiedClient))) {
//            throw new UserException("Nie udalo sie zmodyfikowac podanego klienta.");
//        }
//    }
//
//    public void activateClient(String clientId) {
//        userRepository.update(UUID.fromString(clientId), "archive", false);
//    }
//
//    public void deactivateClient(String clientId) {
//        userRepository.update(UUID.fromString(clientId), "archive", true);
//    }
//
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
//    /*----------------------------------------------HANDLE UUID----------------------------------------------*/
//
//    public ClientDTO getClientById(UUID clientID) {
//        return getClientById(clientID.toString());
//    }
//
//    public void activateClient(UUID clientId) {
//        activateClient(clientId.toString());
//    }
//
//    public void deactivateClient(UUID clientId) {
//        deactivateClient(clientId.toString());
//    }
//
////    public void changeClientPassword(UUID id, ChangePasswordDTORequest changePasswordDTO) {
////        changeClientPassword(id.toString(), changePasswordDTO);
////    }
}
