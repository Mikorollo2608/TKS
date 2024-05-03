package tks.gv.userservice;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class Client extends User {

    public Client(UUID id, String firstName, String lastName, String login, String password) {
        super(id, firstName, lastName, login, password);
    }
}
