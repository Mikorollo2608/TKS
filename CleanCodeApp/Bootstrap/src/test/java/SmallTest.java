import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import tks.gv.AppREST;
import tks.gv.data.dto.out.ClientDTOResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = AppREST.class)
//@AutoConfigureMockMvc
//@TestPropertySource(
//        locations = "classpath:application-integrationtest.properties")
public class SmallTest {

    static final String appUrlClient = "http://localhost:8080/api/clients";

    @BeforeEach
    void init() throws URISyntaxException {
        RestAssured.given().get(new URI(appUrlClient));
    }


    @Test
    void getAllClientsTest() throws URISyntaxException, JsonProcessingException {
//                RestAssured.given().get(new URI(appUrlClient));
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient));

        ObjectMapper objectMapper = new ObjectMapper();
        List<ClientDTOResponse> clientDTOList = objectMapper.readValue(response.asString(), new TypeReference<>() {
        });
        assertEquals(5, clientDTOList.size());

        assertEquals("login15", clientDTOList.get(1).getLogin());

        assertEquals(200, response.getStatusCode());
    }
}
