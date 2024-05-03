package tks.gv.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "id", "login", "clientTypeName"})
public class ClientDTO {
    public interface BasicClientValidation {
    }

    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    @NotBlank(groups = {BasicClientValidation.class})
    private String login;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty("clientTypeName")
    private String clientType;

    @JsonCreator
    public ClientDTO(@JsonProperty("id") String id,
                     @JsonProperty("login") String login,
                     @JsonProperty("archive") boolean archive,
                     @JsonProperty("clientTypeName") String clientType) {
        this.id = id;
        this.login = login;
        this.archive = archive;
        this.clientType = clientType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return archive == clientDTO.archive &&
                Objects.equals(id, clientDTO.id) &&
                Objects.equals(login, clientDTO.login) &&
                Objects.equals(clientType, clientDTO.clientType);
    }
}