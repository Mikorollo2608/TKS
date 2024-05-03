package tks.gv.userservice.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;


@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "id", "login", "firstName", "lastName"})
public class UserDTO {

    public interface BasicUserValidation {}
    public interface PasswordValidation {}

    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    @NotBlank(groups = {BasicUserValidation.class})
    private String login;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty("firstName")
    @NotBlank(groups = {BasicUserValidation.class})
    private String firstName;
    @JsonProperty("lastName")
    @NotBlank(groups = {BasicUserValidation.class})
    private String lastName;
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = {PasswordValidation.class})
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,}$", groups = {PasswordValidation.class})
    private String password;

    @JsonCreator
    public UserDTO(@JsonProperty("id") String id,
                   @JsonProperty("firstName") String firstName,
                   @JsonProperty("lastName") String lastName,
                   @JsonProperty("login") String login,
                   @JsonProperty("password") String password,
                   @JsonProperty("archive") boolean archive) {
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

        UserDTO userDTO = (UserDTO) o;

        if (!Objects.equals(id, userDTO.id)) return false;
        if (archive != userDTO.archive) return false;
        if (!Objects.equals(login, userDTO.login)) return false;
        if (!Objects.equals(firstName, userDTO.firstName)) return false;
        return Objects.equals(lastName, userDTO.lastName);
    }
}