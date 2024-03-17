//package tks.gv.data.dto.out;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
//import lombok.Getter;
//import lombok.experimental.FieldDefaults;
//
//import java.util.Objects;
//
//
//@Getter
//@FieldDefaults(makeFinal = true)
//@JsonPropertyOrder({"archive", "id", "login"})
//public class UserDTOResponse {
//
//    public interface BasicUserValidation {}
//
//    @JsonProperty("id")
//    private String id;
//    @JsonProperty("login")
//    @NotBlank(groups = {BasicUserValidation.class})
//    private String login;
//    @JsonProperty("archive")
//    private boolean archive;
//    @JsonCreator
//    public UserDTOResponse(@JsonProperty("id") String id,
//                           @JsonProperty("login") String login,
//                           @JsonProperty("archive") boolean archive) {
//        this.id = id;
//        this.login = login;
//        this.archive = archive;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        UserDTOResponse userDTO = (UserDTOResponse) o;
//
//        if (!Objects.equals(id, userDTO.id)) return false;
//        if (archive != userDTO.archive) return false;
//        return Objects.equals(login, userDTO.login);
//    }
//}