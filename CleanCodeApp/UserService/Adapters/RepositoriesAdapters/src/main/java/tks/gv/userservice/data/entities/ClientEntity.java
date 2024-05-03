package tks.gv.userservice.data.entities;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
@BsonDiscriminator(key = "_clazz", value = "client")
public class ClientEntity extends UserEntity {
    @BsonProperty("firstname")
    private String firstName;
    @BsonProperty("lastname")
    private String lastName;

    @BsonCreator
    public ClientEntity(@BsonProperty("_id") String id,
                        @BsonProperty("firstname") String firstName,
                        @BsonProperty("lastname") String lastName,
                        @BsonProperty("login") String login,
                        @BsonProperty("password") String password,
                        @BsonProperty("archive") boolean archive) {
        super(id, login, password, archive);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEntity that = (ClientEntity) o;
        return isArchive() == that.isArchive() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(getLogin(), that.getLogin());
    }
}
