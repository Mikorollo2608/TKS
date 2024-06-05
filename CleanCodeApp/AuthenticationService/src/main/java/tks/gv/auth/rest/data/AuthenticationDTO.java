package tks.gv.auth.rest.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDTO {
    @Schema(description = "login", example = "admRes2@test", requiredMode = Schema.RequiredMode.REQUIRED)
    private String login;
    @Schema(description = "password", example = "P@ssw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
