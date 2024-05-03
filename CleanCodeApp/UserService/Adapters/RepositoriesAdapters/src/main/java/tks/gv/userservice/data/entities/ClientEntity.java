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


    @BsonCreator
    public ClientEntity(@BsonProperty("_id") String id,
                        @BsonProperty("firstname") String firstName,
                        @BsonProperty("lastname") String lastName,
                        @BsonProperty("login") String login,
                        @BsonProperty("password") String password,
                        @BsonProperty("archive") boolean archive) {
        super(id, firstName, lastName, login, password, archive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEntity that = (ClientEntity) o;
        return isArchive() == that.isArchive() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getFirstName(), that.getFirstName()) &&
                Objects.equals(getLastName(), that.getLastName()) &&
                Objects.equals(getLogin(), that.getLogin());
    }
}
