package tks.gv.auth.repositories.data;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
public class UserEntity {
    @BsonProperty("_id")
    private final String id;
    @BsonProperty("login")
    private final String login;
    @BsonProperty("password")
    private final String password;
    @BsonProperty("archive")
    private final Boolean archive;
    @BsonProperty("_clazz")
    private final String role;

    @BsonCreator
    public UserEntity(@BsonProperty("_id") String id,
                      @BsonProperty("login") String login,
                      @BsonProperty("password") String password,
                      @BsonProperty("archive") Boolean archive,
                      @BsonProperty("_clazz") String role
    ) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.archive = archive;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity userDTO = (UserEntity) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(login, userDTO.login);
    }
}
