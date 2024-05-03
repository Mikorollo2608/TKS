package tks.gv.data.mappers.entities;

import tks.gv.data.entities.ClientEntity;
import tks.gv.Client;

import java.util.Objects;
import java.util.UUID;

public class ClientMapper {

    public static ClientEntity toEntity(Client client) {
        return new ClientEntity(Objects.requireNonNullElse(client.getId(), "").toString(),
                client.getLogin(),
                client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromEntity(ClientEntity clientEntity) {
        Client clientModel = new Client(UUID.fromString(clientEntity.getId()),
                clientEntity.getLogin(), clientEntity.getClientType());
        clientModel.setArchive(clientEntity.isArchive());
        return clientModel;
    }
}
