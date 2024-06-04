package tks.gv.userservice.data.mappers.dto;

import tks.gv.userservice.Client;
import tks.gv.userservice.data.dto.ClientRegisterInSecondServiceDTO;

public class ClientMessageMapper {

    public static ClientRegisterInSecondServiceDTO toClientRegisterInSecondServiceDTO(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientRegisterInSecondServiceDTO(
                client.getId().toString(),
                client.getLogin(),
                client.isArchive()
        );
    }
}