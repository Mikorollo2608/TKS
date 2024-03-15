package tks.gv.aggregates;

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

import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.repositories.UserMongoRepository;

import tks.gv.users.Client;
import tks.gv.users.User;

import java.util.List;

@Component
public class ClientMongoRepositoryAdapter implements AddUserPort, GetAllUsersPort {

    private final UserMongoRepository repository;

    @Autowired
    public ClientMongoRepositoryAdapter(UserMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addUser(User user) {
        try {
            repository.create(ClientMapper.toUserEntity((Client) user));
        } catch (MyMongoException e) {
            throw new RepositoryAdapterException(this.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        return repository.readAll()
                .stream()
                .map(ClientMongoRepositoryAdapter::chooseMapper)
                .toList();
    }

    private static User chooseMapper(UserEntity userEntity) {
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
}
