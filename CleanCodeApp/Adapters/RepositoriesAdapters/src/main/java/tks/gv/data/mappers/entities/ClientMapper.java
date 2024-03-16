package tks.gv.data.mappers.entities;

import tks.gv.data.entities.ClientEntity;
import tks.gv.users.Client;

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

    public static Client fromUserEntity(ClientEntity clientDTO) {
        Client clientModel = new Client(UUID.fromString(clientDTO.getId()), clientDTO.getFirstName(),
                clientDTO.getLastName(), clientDTO.getLogin(), clientDTO.getPassword(), clientDTO.getClientType());
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}
