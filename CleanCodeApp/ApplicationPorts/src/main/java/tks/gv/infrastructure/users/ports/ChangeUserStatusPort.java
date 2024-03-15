package tks.gv.infrastructure.users.ports;

import java.util.UUID;

public interface ChangeUserStatusPort {

    void activateUser(UUID id);

    void deactivateUser(UUID id);
}
