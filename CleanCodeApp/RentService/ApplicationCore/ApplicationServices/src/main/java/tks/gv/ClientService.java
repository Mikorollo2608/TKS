package tks.gv;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return addUserPort.addUser(client);
    }

    @Override
    public Client getClientById(UUID clientId) {
        return getUserByIdPort.getUserById(clientId);
    }

    @Override
    public List<Client> getAllClients() {
        return getAllUsersPort.getAllUsers();
    }

    @Override
    public Client getClientByLogin(String login) {
        return getUserByLoginPort.getUserByLogin(login);
    }

    @Override
    public List<Client> getClientByLoginMatching(String login) {
        return getUserByLoginPort.getUserByLoginMatching(login);
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

}
