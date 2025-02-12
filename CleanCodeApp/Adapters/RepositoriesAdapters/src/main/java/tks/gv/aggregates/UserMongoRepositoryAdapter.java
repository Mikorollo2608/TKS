package tks.gv.aggregates;

import com.mongodb.client.model.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tks.gv.data.entities.AdminEntity;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.entities.ResourceAdminEntity;
import tks.gv.data.entities.UserEntity;

import tks.gv.data.mappers.entities.AdminMapper;
import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.data.mappers.entities.ResourceAdminMapper;

import tks.gv.exceptions.MyMongoException;
import tks.gv.exceptions.RepositoryAdapterException;
import tks.gv.exceptions.UnexpectedUserTypeException;

import tks.gv.exceptions.UserException;
import tks.gv.exceptions.UserLoginException;
import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.ChangeUserStatusPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.infrastructure.users.ports.GetUserByLoginPort;
import tks.gv.infrastructure.users.ports.ModifyUserPort;
import tks.gv.repositories.UserMongoRepository;

import tks.gv.users.Admin;
import tks.gv.users.Client;
import tks.gv.users.ResourceAdmin;
import tks.gv.users.User;

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
    public User addUser(User user) {
        try {
            return autoMap(repository.create(autoMap(user)));
        } catch (MyMongoException e) {
            throw new RepositoryAdapterException(this.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        return repository.readAll()
                .stream()
                .map(this::autoMap)
                .toList();
    }

    @Override
    public User getUserById(UUID id) {
        return autoMap(repository.readByUUID(id));
    }

    @Override
    public User getUserByLogin(String login) {
        var listUser = repository.read(Filters.eq("login", login));
        return !listUser.isEmpty() ? autoMap(listUser.get(0)) : null;
    }

    @Override
    public List<User> getUserByLoginMatching(String login) {
        return repository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login))))
                .stream()
                .map(this::autoMap)
                .toList();
    }

    @Override
    public void modifyUser(User modifiedUser) {
        var list = repository.read(Filters.and(
                Filters.eq("login", modifiedUser.getLogin()),
                Filters.ne("_id", modifiedUser.getId().toString())));
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego uzytkownika - " +
                    "proba zmiany loginu na login wystepujacy juz u innego uzytkownika");
        }

        if (!repository.updateByReplace(modifiedUser.getId(), autoMap(modifiedUser))) {
            throw new UserException("Nie udalo sie zmodyfikowac podanego uzytkownika.");
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

    protected User autoMap(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        if (userEntity instanceof ClientEntity clientEntity) {
            return ClientMapper.fromUserEntity(clientEntity);
        }
        if (userEntity instanceof AdminEntity adminEntity) {
            return AdminMapper.fromUserEntity(adminEntity);
        }
        if (userEntity instanceof ResourceAdminEntity adminEntity) {
            return ResourceAdminMapper.fromUserEntity(adminEntity);
        }

        throw new UnexpectedUserTypeException("Typ danego uzytkownika nie pasuje do zadnego z obslugiwanych!");
    }

    protected UserEntity autoMap(User user) {
        if (user == null) {
            return null;
        }
        if (user instanceof Client client) {
            return ClientMapper.toUserEntity(client);
        }
        if (user instanceof Admin admin) {
            return AdminMapper.toUserEntity(admin);
        }
        if (user instanceof ResourceAdmin resourceAdmin) {
            return ResourceAdminMapper.toUserEntity(resourceAdmin);
        }

        throw new UnexpectedUserTypeException("Typ danego uzytkownika nie pasuje do zadnego z obslugiwanych!");
    }

}
