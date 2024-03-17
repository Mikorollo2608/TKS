//package tks.gv.data.dto.in;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//
//import jakarta.validation.constraints.NotBlank;
//import lombok.Getter;
//import lombok.experimental.FieldDefaults;
//
//import java.util.Objects;
//
//
//@Getter
//@FieldDefaults(makeFinal = true)
//@JsonPropertyOrder({"archive", "id", "login", "clientTypeName", "firstName", "lastName"})
//public class ClientDTORequest extends UserDTORequest {
//    @JsonProperty("firstName")
//    @NotBlank(groups = {BasicUserValidation.class})
//    private String firstName;
//    @JsonProperty("lastName")
//    @NotBlank(groups = {BasicUserValidation.class})
//    private String lastName;
//    @JsonProperty("clientTypeName")
//    private String clientType;
//
//    @JsonCreator
//    public ClientDTORequest(@JsonProperty("id") String id,
//                            @JsonProperty("firstName") String firstName,
//                            @JsonProperty("lastName") String lastName,
//                            @JsonProperty("login") String login,
//                            @JsonProperty("password") String password,
//                            @JsonProperty("archive") boolean archive,
//                            @JsonProperty("clientTypeName") String clientType) {
//        super(id, login, password, archive);
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.clientType = clientType;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ClientDTORequest that = (ClientDTORequest) o;
//        return isArchive() == that.isArchive() &&
//                Objects.equals(getId(), that.getId()) &&
//                Objects.equals(firstName, that.firstName) &&
//                Objects.equals(lastName, that.lastName) &&
//                Objects.equals(getLogin(), that.getLogin()) &&
//                Objects.equals(clientType, that.clientType);
//    }
//}
