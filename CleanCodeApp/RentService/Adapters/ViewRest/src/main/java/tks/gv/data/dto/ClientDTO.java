package tks.gv.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.common.base.Objects;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "id", "login", "clientTypeName", "firstName", "lastName"})
public class ClientDTO {
    public interface BasicUserValidation {
    }

    public interface PasswordValidation {
    }

    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    @NotBlank(groups = {BasicUserValidation.class})
    private String login;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = {PasswordValidation.class})
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,}$", groups = {PasswordValidation.class})
    private String password;

    @JsonProperty("firstName")
    @NotBlank(groups = {BasicUserValidation.class})
    private String firstName;
    @JsonProperty("lastName")
    @NotBlank(groups = {BasicUserValidation.class})
    private String lastName;
    @JsonProperty("clientTypeName")
    private String clientType;

    @JsonCreator
    public ClientDTO(@JsonProperty("id") String id,
                     @JsonProperty("firstName") String firstName,
                     @JsonProperty("lastName") String lastName,
                     @JsonProperty("login") String login,
                     @JsonProperty("password") String password,
                     @JsonProperty("archive") boolean archive,
                     @JsonProperty("clientTypeName") String clientType) {
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
        ClientDTO clientDTO = (ClientDTO) o;
        return archive == clientDTO.archive &&
                Objects.equal(id, clientDTO.id) &&
                Objects.equal(login, clientDTO.login) &&
                Objects.equal(password, clientDTO.password) &&
                Objects.equal(firstName, clientDTO.firstName) &&
                Objects.equal(lastName, clientDTO.lastName) &&
                Objects.equal(clientType, clientDTO.clientType);
    }
}