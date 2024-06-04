package tks.gv.userservice.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;


@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "id", "login", "clientTypeName"})
public class ClientRegisterInSecondServiceDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    private String login;
    @JsonProperty("archive")
    private boolean archive;

    @JsonCreator
    public ClientRegisterInSecondServiceDTO(@JsonProperty("id") String id,
                                            @JsonProperty("login") String login,
                                            @JsonProperty("archive") boolean archive) {
        this.id = id;
        this.login = login;
        this.archive = archive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRegisterInSecondServiceDTO clientRegisterInSecondServiceDTO = (ClientRegisterInSecondServiceDTO) o;
        return archive == clientRegisterInSecondServiceDTO.archive &&
                Objects.equals(id, clientRegisterInSecondServiceDTO.id) &&
                Objects.equals(login, clientRegisterInSecondServiceDTO.login);
    }
}