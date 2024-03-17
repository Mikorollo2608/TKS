package tks.gv.data.mappers.dto;

import tks.gv.data.dto.in.ClientDTORequest;
import tks.gv.data.dto.out.ClientDTOResponse;
import tks.gv.users.Client;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTOResponse toUserDTO(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientDTOResponse(client.getId().toString(),
                client.getFirstName(),
                client.getLastName(),
                client.getLogin(),
                client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromUserDTO(ClientDTORequest clientDTO) {
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
