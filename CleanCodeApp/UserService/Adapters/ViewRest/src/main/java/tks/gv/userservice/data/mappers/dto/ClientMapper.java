package tks.gv.userservice.data.mappers.dto;



import tks.gv.userservice.data.dto.ClientDTO;
import tks.gv.userservice.Client;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTO toUserDTO(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientDTO(client.getId().toString(),
                client.getFirstName(),
                client.getLastName(),
                client.getLogin(),
                client.getPassword(),
                client.isArchive());
    }

    public static Client fromUserDTO(ClientDTO clientDTO) {
        if (clientDTO == null) {
            return null;
        }

        Client clientModel = new Client(clientDTO.getId() != null ? UUID.fromString(clientDTO.getId()) : null,
                clientDTO.getFirstName(),
                clientDTO.getLastName(),
                clientDTO.getLogin(),
                clientDTO.getPassword());
        clientModel.setArchive(clientDTO.isArchive());

        return clientModel;
    }
}