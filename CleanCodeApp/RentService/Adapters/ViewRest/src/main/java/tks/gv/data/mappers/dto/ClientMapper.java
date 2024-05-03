package tks.gv.data.mappers.dto;



import tks.gv.data.dto.ClientDTO;
import tks.gv.Client;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTO toDTO(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientDTO(client.getId().toString(),
                client.getFirstName(),
                client.getLastName(),
                client.getLogin(),
                client.getPassword(),
                client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromDTO(ClientDTO clientDTO) {
        if (clientDTO == null) {
            return null;
        }

        Client clientModel = new Client(clientDTO.getId() != null ? UUID.fromString(clientDTO.getId()) : null,
                clientDTO.getFirstName(),
                clientDTO.getLastName(),
                clientDTO.getLogin(),
                clientDTO.getPassword(),
                clientDTO.getClientType());
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}