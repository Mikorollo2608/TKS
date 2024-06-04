package tks.gv.rentservice;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tks.gv.rentservice.infrastructure.client.ports.AddClientPort;
import tks.gv.rentservice.infrastructure.client.ports.ChangeClientStatusPort;
import tks.gv.rentservice.infrastructure.client.ports.DeleteClientPort;
import tks.gv.rentservice.infrastructure.client.ports.GetAllClientsPort;
import tks.gv.rentservice.infrastructure.client.ports.GetClientByIdPort;
import tks.gv.rentservice.infrastructure.client.ports.GetClientByLoginPort;
import tks.gv.rentservice.infrastructure.client.ports.ModifyClientPort;
import tks.gv.rentservice.ui.clients.ports.ChangeClientStatusUseCase;
import tks.gv.rentservice.ui.clients.ports.DeleteClientUseCase;
import tks.gv.rentservice.ui.clients.ports.GetAllClientsUseCase;
import tks.gv.rentservice.ui.clients.ports.GetClientByIdUseCase;
import tks.gv.rentservice.ui.clients.ports.GetClientByLoginUseCase;
import tks.gv.rentservice.ui.clients.ports.ModifyClientUseCase;
import tks.gv.rentservice.ui.clients.ports.RegisterClientUseCase;

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
        ChangeClientStatusUseCase,
        DeleteClientUseCase {

    private AddClientPort addClientPort;
    private GetAllClientsPort getAllClientsPort;
    private GetClientByIdPort getClientByIdPort;
    private GetClientByLoginPort getClientByLoginPort;
    private ModifyClientPort modifyClientPort;
    private ChangeClientStatusPort changeClientStatusPort;
    private DeleteClientPort deleteClientPort;

    @Autowired
    public ClientService(AddClientPort addClientPort, GetAllClientsPort getAllClientsPort, GetClientByIdPort getClientByIdPort,
                         GetClientByLoginPort getClientByLoginPort, ModifyClientPort modifyClientPort,
                         ChangeClientStatusPort changeClientStatusPort, DeleteClientPort deleteClientPort) {
        this.addClientPort = addClientPort;
        this.getAllClientsPort = getAllClientsPort;
        this.getClientByIdPort = getClientByIdPort;
        this.getClientByLoginPort = getClientByLoginPort;
        this.modifyClientPort = modifyClientPort;
        this.changeClientStatusPort = changeClientStatusPort;
        this.deleteClientPort = deleteClientPort;
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

    @Override
    public void deleteClient(String login) {
        deleteClientPort.deleteClientPort(login);
    }
}
