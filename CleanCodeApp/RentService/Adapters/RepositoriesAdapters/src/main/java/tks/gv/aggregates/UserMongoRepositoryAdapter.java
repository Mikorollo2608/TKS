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
import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.ChangeUserStatusPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.infrastructure.users.ports.GetUserByLoginPort;
import tks.gv.infrastructure.users.ports.ModifyUserPort;
import tks.gv.repositories.UserMongoRepository;

import tks.gv.Client;

import java.util.List;
import java.util.UUID;

@Component
public class UserMongoRepositoryAdapter implements
        AddUserPort, GetAllUsersPort, GetUserByIdPort, GetUserByLoginPort, ModifyUserPort, ChangeUserStatusPort {

    private final UserMongoRepository repository;

    @Autowired
    public UserMongoRepositoryAdapter(UserMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Client addUser(Client user) {
        try {
            return autoMap(repository.create(autoMap(user)));
        } catch (MyMongoException e) {
            throw new RepositoryAdapterException(this.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @Override
    public List<Client> getAllUsers() {
        return repository.readAll()
                .stream()
                .map(this::autoMap)
                .toList();
    }

    @Override
    public Client getUserById(UUID id) {
        return autoMap(repository.readByUUID(id));
    }

    @Override
    public Client getUserByLogin(String login) {
        var listUser = repository.read(Filters.eq("login", login));
        return !listUser.isEmpty() ? autoMap(listUser.get(0)) : null;
    }

    @Override
    public List<Client> getUserByLoginMatching(String login) {
        return repository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login))))
                .stream()
                .map(this::autoMap)
                .toList();
    }

    @Override
    public void modifyUser(Client modifiedUser) {
        var list = repository.read(Filters.and(
                Filters.eq("login", modifiedUser.getLogin()),
                Filters.ne("_id", modifiedUser.getId().toString())));
        if (!list.isEmpty()) {
            throw new ClientLoginException("Nie udalo sie zmodyfikowac podanego uzytkownika - " +
                    "proba zmiany loginu na login wystepujacy juz u innego uzytkownika");
        }

        if (!repository.updateByReplace(modifiedUser.getId(), autoMap(modifiedUser))) {
            throw new ClientException("Nie udalo sie zmodyfikowac podanego uzytkownika.");
        }
    }

    @Override
    public void activateUser(UUID id) {
        repository.update(id, "archive", false);
    }

    @Override
    public void deactivateUser(UUID id) {
        repository.update(id, "archive", true);
    }

    protected Client autoMap(ClientEntity clientEntity) {
        if (clientEntity == null) {
            return null;
        }
        return ClientMapper.fromUserEntity(clientEntity);
    }

    protected ClientEntity autoMap(Client client) {
        if (client == null) {
            return null;
        }

        return ClientMapper.toUserEntity(client);
    }

}
