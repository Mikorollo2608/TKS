package tks.gv.userservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.Objects;

@Getter
@NoArgsConstructor
public abstract class User {

    private UUID id;
    @Setter
    private String login;
    @Setter
    private String password;
    @Setter
    private boolean archive = false;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
}
