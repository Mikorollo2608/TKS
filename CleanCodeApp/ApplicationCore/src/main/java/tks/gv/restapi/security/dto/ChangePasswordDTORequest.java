package tks.gv.restapi.security.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tks.gv.restapi.data.dto.UserDTO;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTORequest {

    private String actualPassword;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,}$", groups = {UserDTO.PasswordValidation.class})
    private String newPassword;
    private String confirmationPassword;
}
