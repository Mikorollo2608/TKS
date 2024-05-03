package tks.gv.userservice.aggregates;

import com.mongodb.client.model.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tks.gv.userservice.data.entities.AdminEntity;
import tks.gv.userservice.data.entities.ClientEntity;
import tks.gv.userservice.data.entities.UserEntity;
import tks.gv.userservice.data.mappers.entities.AdminMapper;
import tks.gv.userservice.data.mappers.entities.ClientMapper;
import tks.gv.userservice.data.mappers.entities.ResourceAdminMapper;
import tks.gv.userservice.repositories.UserMongoRepository;
import tks.gv.userservice.data.entities.ResourceAdminEntity;

import tks.gv.userservice.exceptions.MyMongoException;
import tks.gv.userservice.exceptions.RepositoryAdapterException;
import tks.gv.userservice.exceptions.UnexpectedUserTypeException;

import tks.gv.userservice.exceptions.UserException;
import tks.gv.userservice.exceptions.UserLoginException;
import tks.gv.userservice.infrastructure.ports.AddUserPort;
import tks.gv.userservice.infrastructure.ports.ChangeUserStatusPort;
import tks.gv.userservice.infrastructure.ports.GetAllUsersPort;

import tks.gv.userservice.infrastructure.ports.GetUserByIdPort;
import tks.gv.userservice.infrastructure.ports.GetUserByLoginPort;
import tks.gv.userservice.infrastructure.ports.ModifyUserPort;

import tks.gv.userservice.Admin;
import tks.gv.userservice.Client;
import tks.gv.userservice.ResourceAdmin;
import tks.gv.userservice.User;

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
