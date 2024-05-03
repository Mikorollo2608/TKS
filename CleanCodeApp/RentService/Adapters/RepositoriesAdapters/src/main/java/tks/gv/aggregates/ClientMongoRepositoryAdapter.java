package tks.gv.aggregates;

import com.mongodb.client.model.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tks.gv.data.entities.ClientEntity;

import tks.gv.data.mappers.entities.ClientMapper;

import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.RepositoryAdapterException;

import tks.gv.exceptions.ClientException;
import tks.gv.exceptions.ClientLoginException;
import tks.gv.infrastructure.clients.ports.AddClientPort;
import tks.gv.infrastructure.clients.ports.ChangeClientStatusPort;
import tks.gv.infrastructure.clients.ports.GetAllClientsPort;

import tks.gv.infrastructure.clients.ports.GetClientByIdPort;
import tks.gv.infrastructure.clients.ports.GetClientByLoginPort;
import tks.gv.infrastructure.clients.ports.ModifyClientPort;
import tks.gv.repositories.ClientMongoRepository;

import tks.gv.Client;

import java.util.List;
import java.util.UUID;

@Component
public class ClientMongoRepositoryAdapter implements
        AddClientPort, GetAllClientsPort, GetClientByIdPort, GetClientByLoginPort, ModifyClientPort, ChangeClientStatusPort {

    private final ClientMongoRepository repository;

    @Autowired
    public ClientMongoRepositoryAdapter(ClientMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Client addClient(Client client) {
        try {
            return autoMap(repository.create(autoMap(client)));
        } catch (MyMongoException e) {
            throw new RepositoryAdapterException(this.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @Override
    public List<Client> getAllClients() {
        return repository.readAll()
                .stream()
                .map(this::autoMap)
                .toList();
    }

    @Override
    public Client getClientById(UUID id) {
        return autoMap(repository.readByUUID(id));
    }

    @Override
    public Client getClientByLogin(String login) {
        var listClient = repository.read(Filters.eq("login", login));
        return !listClient.isEmpty() ? autoMap(listClient.get(0)) : null;
    }

    @Override
    public List<Client> getClientByLoginMatching(String login) {
        return repository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login))))
                .stream()
                .map(this::autoMap)
                .toList();
    }

    @Override
    public void modifyClient(Client modifiedClient) {
        var list = repository.read(Filters.and(
                Filters.eq("login", modifiedClient.getLogin()),
                Filters.ne("_id", modifiedClient.getId().toString())));
        if (!list.isEmpty()) {
            throw new ClientLoginException("Nie udalo sie zmodyfikowac podanego uzytkownika - " +
                    "proba zmiany loginu na login wystepujacy juz u innego uzytkownika");
        }

        if (!repository.updateByReplace(modifiedClient.getId(), autoMap(modifiedClient))) {
            throw new ClientException("Nie udalo sie zmodyfikowac podanego uzytkownika.");
        }
    }

    @Override
    public void activateClient(UUID id) {
        repository.update(id, "archive", false);
    }

    @Override
    public void deactivateClient(UUID id) {
        repository.update(id, "archive", true);
    }

    protected Client autoMap(ClientEntity clientEntity) {
        if (clientEntity == null) {
            return null;
        }
        return ClientMapper.fromEntity(clientEntity);
    }

    protected ClientEntity autoMap(Client client) {
        if (client == null) {
            return null;
        }

        return ClientMapper.toEntity(client);
    }

}
