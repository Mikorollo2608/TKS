package tks.gv.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.data.entities.ClientEntity;

import tks.gv.data.mappers.entities.ClientMapper;
import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.repostioryInMemory.InMemoryRepository;
import tks.gv.users.Client;
import tks.gv.users.User;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ClientInMemoryRepositoryAdapter implements AddUserPort, GetAllUsersPort {

    private final InMemoryRepository<ClientEntity> repository;

    @Autowired
    public ClientInMemoryRepositoryAdapter(InMemoryRepository<ClientEntity> repository) {
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

    @PostConstruct
    void init() {
        repository.create(ClientMapper.toUserEntity(new Client(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), "Johnny", "Brown", "login", "haselko", "normal")));
        repository.create(ClientMapper.toUserEntity(new Client(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), "Rose", "Tetris", "login15", "haselko", "athlete")));
        repository.create(ClientMapper.toUserEntity(new Client(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), "John", "Lee", "leeJo15", "haselko", "coach")));
        repository.create(ClientMapper.toUserEntity(new Client(UUID.fromString("3a722080-9668-42a2-9788-4695a4b9f5a7"), "Krzysztof", "Scala", "scKrzy", "haselko", "normal")));
        repository.create(ClientMapper.toUserEntity(new Client(UUID.fromString("126778af-0e19-46d4-b329-0b6b92548f9a"), "Adam", "Scout", "scAdam", "haselko", "normal")));
    }
}
