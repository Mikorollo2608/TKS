package tks.gv.rentservice.infrastructure.client.ports;

import java.util.UUID;

public interface ChangeClientStatusPort {

    void activateClient(UUID id);

    void deactivateClient(UUID id);
}
