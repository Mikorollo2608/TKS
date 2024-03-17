package tks.gv.data.dto.in;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ClientRegisterDTORequest extends ClientDTORequest {

    @JsonCreator
    public ClientRegisterDTORequest(@JsonProperty("firstName") String firstName,
                                    @JsonProperty("lastName") String lastName,
                                    @JsonProperty("login") String login,
                                    @JsonProperty("password") String password) {
        super(null, firstName, lastName, login, password, false, "normal");
    }
}
