package tks.gv.userservice.data.mappers.entities;

import tks.gv.userservice.data.entities.ClientEntity;
import tks.gv.userservice.Client;

import java.util.Objects;
import java.util.UUID;

public class ClientMapper {

    public static ClientEntity toUserEntity(Client client) {
        return new ClientEntity(Objects.requireNonNullElse(client.getId(), "").toString(),
                client.getFirstName(),
                client.getLastName(), client.getLogin(),
                client.getPassword(),
                client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromUserEntity(ClientEntity clientEntity) {
        Client clientModel = new Client(UUID.fromString(clientEntity.getId()), clientEntity.getFirstName(),
                clientEntity.getLastName(), clientEntity.getLogin(), clientEntity.getPassword(), clientEntity.getClientType());
        clientModel.setArchive(clientEntity.isArchive());
        return clientModel;
    }
}
