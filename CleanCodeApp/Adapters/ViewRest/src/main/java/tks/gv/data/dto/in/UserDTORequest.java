package tks.gv.data.dto.in;

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
@JsonPropertyOrder({"archive", "id", "login"})
public class UserDTORequest {

    public interface BasicUserValidation {}
    public interface PasswordValidation {}

    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    @NotBlank(groups = {BasicUserValidation.class})
    private String login;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty(value = "password")
    @NotBlank(groups = {PasswordValidation.class})
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,}$", groups = {PasswordValidation.class})
    private String password;
    @JsonCreator
    public UserDTORequest(@JsonProperty("id") String id,
                          @JsonProperty("login") String login,
                          @JsonProperty("password") String password,
                          @JsonProperty("archive") boolean archive) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.archive = archive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTORequest userDTO = (UserDTORequest) o;

        if (!Objects.equals(id, userDTO.id)) return false;
        if (archive != userDTO.archive) return false;
        if (!Objects.equals(login, userDTO.login)) return false;
        return Objects.equals(password, userDTO.password);
    }
}