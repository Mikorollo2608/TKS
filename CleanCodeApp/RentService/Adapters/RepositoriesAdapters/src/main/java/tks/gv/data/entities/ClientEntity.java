package tks.gv.data.entities;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class ClientEntity implements Entity {
    @BsonProperty("_id")
    private String id;
    @BsonProperty("login")
    private String login;
    @BsonProperty("archive")
    private boolean archive;
    @BsonProperty("password")
    private String password;
    @BsonProperty("firstname")
    private String firstName;
    @BsonProperty("lastname")
    private String lastName;
    @BsonProperty("clienttype")
    private String clientType;

    @BsonCreator
    public ClientEntity(@BsonProperty("_id") String id,
                        @BsonProperty("firstname") String firstName,
                        @BsonProperty("lastname") String lastName,
                        @BsonProperty("login") String login,
                        @BsonProperty("password") String password,
                        @BsonProperty("archive") boolean archive,
                        @BsonProperty("clienttype") String clientType) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.archive = archive;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = clientType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEntity that = (ClientEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(login, that.login) &&
                isArchive() == that.isArchive() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(getLogin(), that.getLogin()) &&
                Objects.equals(clientType, that.clientType);
    }
}
