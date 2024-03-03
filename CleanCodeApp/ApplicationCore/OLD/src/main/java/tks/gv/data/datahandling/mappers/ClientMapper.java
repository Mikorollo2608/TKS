package tks.gv.data.datahandling.mappers;

import tks.gv.model.logic.users.Client;
import tks.gv.data.datahandling.entities.ClientEntity;

import java.util.UUID;

public class ClientMapper {

    public static ClientEntity toMongoUser(Client client) {
        return new ClientEntity(client.getId().toString(), client.getFirstName(),
                client.getLastName(), client.getLogin(),
                client.getPassword(),
                client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromMongoUser(ClientEntity clientDTO) {
        Client clientModel = new Client(UUID.fromString(clientDTO.getId()), clientDTO.getFirstName(),
                clientDTO.getLastName(), clientDTO.getLogin(), clientDTO.getPassword(), clientDTO.getClientType());
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}
