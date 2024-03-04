package tks.gv.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.data.entities.ClientEntity;

import tks.gv.data.mappers.ClientMapper;
import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.repostioryInMemory.UserInMemoryRepository;
import tks.gv.users.Client;
import tks.gv.users.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientInMemoryRepositoryAdapter implements AddUserPort, GetAllUsersPort {

    private final UserInMemoryRepository<ClientEntity> repository;

    @Autowired
    public ClientInMemoryRepositoryAdapter(UserInMemoryRepository<ClientEntity> repository) {
        this.repository = repository;
    }

    @Override
    public void addUser(User user) {
        repository.create(ClientMapper.toUserEntity((Client) user));
    }

    @Override
    public List<User> getAllUsers() {
        return repository.readAll()
                .stream()
                .map(ClientMapper::fromUserEntity)
                .collect(Collectors.toList());
    }
}
