package tks.gv.users;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class Admin extends User {

    public Admin(UUID id, String login, String password) {
        super(id, login, password);
    }
}