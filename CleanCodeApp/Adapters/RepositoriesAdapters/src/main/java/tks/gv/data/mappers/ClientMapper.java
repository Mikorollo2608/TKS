package tks.gv.data.mappers;

import tks.gv.data.entities.ClientEntity;
import tks.gv.users.Client;

import java.util.UUID;

public class ClientMapper {

    public static ClientEntity toUserEntity(Client client) {
        return new ClientEntity(client.getId().toString(), client.getFirstName(),
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
