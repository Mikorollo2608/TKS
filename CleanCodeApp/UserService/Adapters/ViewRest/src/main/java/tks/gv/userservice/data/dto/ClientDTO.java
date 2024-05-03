package tks.gv.userservice.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;


@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "id", "login", "firstName", "lastName"})
public class ClientDTO extends UserDTO {


    @JsonCreator
    public ClientDTO(@JsonProperty("id") String id,
                     @JsonProperty("firstName") String firstName,
                     @JsonProperty("lastName") String lastName,
                     @JsonProperty("login") String login,
                     @JsonProperty("password") String password,
                     @JsonProperty("archive") boolean archive) {
        super(id, firstName, lastName, login, password, archive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO that = (ClientDTO) o;
        return isArchive() == that.isArchive() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getFirstName(), that.getFirstName()) &&
                Objects.equals(getLastName(), that.getLastName()) &&
                Objects.equals(getLogin(), that.getLogin());
    }
}