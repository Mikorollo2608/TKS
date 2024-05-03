package tks.gv.userservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Client extends User {

    private String firstName;
    private String lastName;

    public Client(UUID id, String firstName, String lastName, String login, String password) {
        super(id, login, password);

        this.firstName = firstName;
        this.lastName = lastName;
    }
}
