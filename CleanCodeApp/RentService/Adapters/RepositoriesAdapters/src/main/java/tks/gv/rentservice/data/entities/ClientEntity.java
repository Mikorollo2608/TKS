package tks.gv.rentservice.data.entities;

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
    @BsonProperty("clienttype")
    private String clientType;

    @BsonCreator
    public ClientEntity(@BsonProperty("_id") String id,
                        @BsonProperty("login") String login,
                        @BsonProperty("archive") boolean archive,
                        @BsonProperty("clienttype") String clientType) {
        this.id = id;
        this.login = login;
        this.archive = archive;
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
                Objects.equals(getLogin(), that.getLogin()) &&
                Objects.equals(clientType, that.clientType);
    }
}
