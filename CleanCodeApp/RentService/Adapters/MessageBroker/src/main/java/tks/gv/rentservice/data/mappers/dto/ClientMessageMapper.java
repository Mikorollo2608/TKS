package tks.gv.rentservice.data.mappers.dto;


import tks.gv.rentservice.Client;
import tks.gv.rentservice.data.dto.ClientRegisterDTO;

import java.util.UUID;

public class ClientMessageMapper {

    public static Client fromDTO(ClientRegisterDTO clientRegisterDTO) {
        if (clientRegisterDTO == null) {
            return null;
        }

        Client client = new Client(
                UUID.fromString(clientRegisterDTO.getId()),
                clientRegisterDTO.getLogin()
        );

        client.setArchive(clientRegisterDTO.isArchive());

        return client;
    }
}