package tks.gv.userservice.data.entities;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class UserEntity implements Entity {
    @BsonProperty("_id")
    private String id;
    @BsonProperty("login")
    private String login;
    @BsonProperty("password")
    private String password;
    @BsonProperty("firstname")
    private String firstName;
    @BsonProperty("lastname")
    private String lastName;
    @BsonProperty("archive")
    private boolean archive;

    @BsonCreator
    public UserEntity(@BsonProperty("_id") String id,
                      @BsonProperty("firstname") String firstName,
                      @BsonProperty("lastname") String lastName,
                      @BsonProperty("login") String login,
                      @BsonProperty("password") String password,
                      @BsonProperty("archive") boolean archive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.archive = archive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity userDTO = (UserEntity) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(login, userDTO.login);
    }

}
