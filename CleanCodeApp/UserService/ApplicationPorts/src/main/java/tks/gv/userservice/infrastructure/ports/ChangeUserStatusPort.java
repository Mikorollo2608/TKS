package tks.gv.userservice.infrastructure.ports;

import java.util.UUID;

public interface ChangeUserStatusPort {

    void activateUser(UUID id);

    void deactivateUser(UUID id);
}
