package tks.gv.userservice.data.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@FieldDefaults(makeFinal = true)
public class ResourceAdminDTO extends UserDTO {

    @JsonCreator
    public ResourceAdminDTO(@JsonProperty("id") String id,
                            @JsonProperty("firstName") String firstName,
                            @JsonProperty("lastName") String lastName,
                            @JsonProperty("login") String login,
                            @JsonProperty("password") String password,
                            @JsonProperty("archive") boolean archive) {
        super(id, firstName, lastName, login, password, archive);
    }
}