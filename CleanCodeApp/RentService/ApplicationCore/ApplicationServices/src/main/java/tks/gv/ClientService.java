package tks.gv;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tks.gv.infrastructure.clients.ports.AddClientPort;
import tks.gv.infrastructure.clients.ports.ChangeClientStatusPort;
import tks.gv.infrastructure.clients.ports.GetAllClientsPort;
import tks.gv.infrastructure.clients.ports.GetClientByIdPort;
import tks.gv.infrastructure.clients.ports.GetClientByLoginPort;
import tks.gv.infrastructure.clients.ports.ModifyClientPort;
import tks.gv.ui.clients.ports.ChangeClientStatusUseCase;
import tks.gv.ui.clients.ports.GetAllClientsUseCase;
import tks.gv.ui.clients.ports.GetClientByIdUseCase;
import tks.gv.ui.clients.ports.GetClientByLoginUseCase;
import tks.gv.ui.clients.ports.ModifyClientUseCase;
import tks.gv.ui.clients.ports.RegisterClientUseCase;

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

    private AddClientPort addClientPort;
    private GetAllClientsPort getAllClientsPort;
    private GetClientByIdPort getClientByIdPort;
    private GetClientByLoginPort getClientByLoginPort;
    private ModifyClientPort modifyClientPort;
    private ChangeClientStatusPort changeClientStatusPort;

    @Autowired
    public ClientService(AddClientPort addClientPort, GetAllClientsPort getAllClientsPort, GetClientByIdPort getClientByIdPort,
                         GetClientByLoginPort getClientByLoginPort, ModifyClientPort modifyClientPort,
                         ChangeClientStatusPort changeClientStatusPort) {
        this.addClientPort = addClientPort;
        this.getAllClientsPort = getAllClientsPort;
        this.getClientByIdPort = getClientByIdPort;
        this.getClientByLoginPort = getClientByLoginPort;
        this.modifyClientPort = modifyClientPort;
        this.changeClientStatusPort = changeClientStatusPort;
    }

    @Override
    public Client registerClient(Client client) {
        return addClientPort.addClient(client);
    }

    @Override
    public Client getClientById(UUID clientId) {
        return getClientByIdPort.getClientById(clientId);
    }

    @Override
    public List<Client> getAllClients() {
        return getAllClientsPort.getAllClients();
    }

    @Override
    public Client getClientByLogin(String login) {
        return getClientByLoginPort.getClientByLogin(login);
    }

    @Override
    public List<Client> getClientByLoginMatching(String login) {
        return getClientByLoginPort.getClientByLoginMatching(login);
    }

    @Override
    public void modifyClient(Client modifiedClient) {
        modifyClientPort.modifyClient(modifiedClient);
    }

    @Override
    public void activateClient(UUID clientId) {
        changeClientStatusPort.activateClient(clientId);
    }

    @Override
    public void deactivateClient(UUID clientId) {
        changeClientStatusPort.deactivateClient(clientId);
    }

}
