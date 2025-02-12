package tks.gv.integrationtests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tks.gv.AppREST;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static tks.gv.integrationtests.NewCleaningClassForTests.cleanUsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {AppREST.class, NewCleaningClassForTests.class})
@TestPropertySource(locations = {"classpath:application-integrationtest.properties"})
public class ClientControllerTests {

    final String appUrlClient = "http://localhost:8081/api-test/clients";

    @AfterAll
    static void cleanAtTheEnd() {
        cleanUsers();
    }

    @Autowired
    NewCleaningClassForTests newCleaningClassForTests;

    @BeforeEach
    void cleanAndInitDatabase() {
        cleanUsers();
        newCleaningClassForTests.initClients();
    }

    @Test
    void getAllClientsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient));
        String responseString = response.asString();

        //First Client
        assertTrue(responseString.contains("\"archive\":false"));
        assertTrue(responseString.contains("\"id\":\""));
        assertTrue(responseString.contains("\"login\":\"loginek\""));
        assertTrue(responseString.contains("\"clientTypeName\":\"normal\""));
        assertTrue(responseString.contains("\"firstName\":\"Adam\""));
        assertTrue(responseString.contains("\"lastName\":\"Smith\""));

        //Third Client
        assertTrue(responseString.contains("\"login\":\"michas13\""));
        assertTrue(responseString.contains("\"clientTypeName\":\"coach\""));
        assertTrue(responseString.contains("\"firstName\":\"Michal\""));
        assertTrue(responseString.contains("\"lastName\":\"Pi\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllClientsTestNoCont() throws URISyntaxException {
        cleanUsers();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createClientTestPos() throws URISyntaxException {
        cleanUsers();
        String JSON = """
                {
                  "firstName": "John",
                  "lastName": "Bravo",
                  "login": "johnBravo",
                  "password": "johnBravo1",
                  "clientTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post(appUrlClient + "/addClient");

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertTrue(responseString.contains("\"login\":\"johnBravo\""));
        assertTrue(responseString.contains("\"clientTypeName\":\"normal\""));
        assertTrue(responseString.contains("\"firstName\":\"John\""));
        assertTrue(responseString.contains("\"lastName\":\"Bravo\""));
    }

    @Test
    void createClientTestNegInvalidData() throws URISyntaxException {
        String json = """
                {
                  "firstName": "John",
                  "lastName": "  ",
                  "login": "johnBravo",
                  "password": "johnBravo1",
                  "clientTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
        assertFalse(responseString.contains("\"lastName\":\"  \""));

        Response responsePost = requestPost.post(appUrlClient + "/addClient");

        assertEquals(400, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
        assertFalse(responseString.contains("\"lastName\":\"  \""));
    }

    @Test
    void createClientTestNegSameLogin() throws URISyntaxException {
        String json = """
                {
                  "firstName": "John",
                  "lastName": "Bravo",
                  "login": "michas13",
                  "password": "michaS13",
                  "clientTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertTrue(responseString.contains("\"login\":\"michas13\""));

        assertFalse(responseString.contains("\"firstName\":\"John\""));
        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));

        Response responsePost = requestPost.post(appUrlClient + "/addClient");

        assertEquals(409, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertFalse(responseString.contains("\"firstName\":\"John\""));
        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));
    }

    @Test
    void getClientByLoginTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient + "/get?login=michas13"));
        String responseString = response.asString();

        assertTrue(responseString.contains("\"login\":\"michas13\",\"clientTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getClientByLoginTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient + "/get?login=564545415612121121"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getClientByIdTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();

        //Retrieve UUID
        String responseLogin = request.get(new URI(appUrlClient + "/get?login=michas13")).asString();
        int index = responseLogin.indexOf("\"id\":\"") + 6;
        String clientId = responseLogin.substring(index, index + 36);

        Response responseById = request.get(new URI(appUrlClient + "/" + clientId));
        String responseByIdString = responseById.asString();

        assertTrue(responseByIdString.contains("\"login\":\"michas13\",\"clientTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));

        assertEquals(200, responseById.getStatusCode());
    }

    @Test
    void getClientByIdTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient + "/" + UUID.randomUUID()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getClientByLoginMatchingPos() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient + "/match?login=login"));
        String responseString = response.asString();

        String[] splitRespStr = responseString.split("},");

        assertEquals(2, splitRespStr.length);

        //First Client
        assertTrue(splitRespStr[0].contains(
                "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));

        //Second Client
        assertTrue(splitRespStr[1].contains(
                "\"login\":\"loginek13\"," +
                        "\"clientTypeName\":\"athlete\"," +
                        "\"firstName\":\"Eva\"," +
                        "\"lastName\":\"Braun\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getClientByLoginMatchingNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlClient + "/match?login=uwu"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());

        assertEquals(204, response.getStatusCode());
    }

    @Test
    void modifyClientTest() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "8d83bbda-e38a-4cf2-9136-40e5310c5761",
                  "firstName": "John",
                  "lastName": "Smith",
                  "login": "loginek",
                  "clientTypeName": "coach"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlClient)).asString();

        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI(appUrlClient + "/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
        String clientId = "8d83bbda-e38a-4cf2-9136-40e5310c5761";

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"coach\"," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\""));

        Response responsePut = requestPut.put(appUrlClient + "/modifyClient");

        assertEquals(204, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"coach\"," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\""));
    }

    @Test
    void modifyClientTestNegInvalidData() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "8d83bbda-e38a-4cf2-9136-40e5310c5761",
                  "firstName": "   ",
                  "lastName": "Smith",
                  "login": "loginek",
                  "clientTypeName": "coach"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlClient)).asString();

        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI(appUrlClient + "/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
        String clientId = "8d83bbda-e38a-4cf2-9136-40e5310c5761";

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"coach\"," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\""));

        Response responsePut = requestPut.put(appUrlClient + "/modifyClient");

        assertEquals(400, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"coach\"," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\""));
    }

    @Test
    void modifyClientTestNegRepeatLoginOfAnotherClient() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "8d83bbda-e38a-4cf2-9136-40e5310c5761",
                  "firstName": "John",
                  "lastName": "Smith",
                  "login": "michas13",
                  "clientTypeName": "coach"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlClient)).asString();

        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI(appUrlClient + "/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
        String clientId = "8d83bbda-e38a-4cf2-9136-40e5310c5761";

        assertTrue(responseString.contains("\"login\":\"michas13\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"michas13\"," +
                        "\"clientTypeName\":\"coach\"," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\""));

        Response responsePut = requestPut.put(appUrlClient + "/modifyClient");

        assertEquals(409, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertTrue(responseString.contains("\"login\":\"michas13\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"clientTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\"," +
                        "\"login\":\"michas13\"," +
                        "\"clientTypeName\":\"coach\"," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\""));
    }

    @Test
    void archiveAndActivateClientTest() throws URISyntaxException {
        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlClient)).asString();

        //Retrieve UUID
        String responseLogin = requestGet.get(new URI(appUrlClient + "/get?login=loginek")).asString();
        int index = responseLogin.indexOf("\"id\":\"") + 6;
        String clientId = responseLogin.substring(index, index + 36);

        /*Archive test*/
        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\""));

        RequestSpecification requestPost = RestAssured.given();
        Response responsePost = requestPost.post(appUrlClient + "/deactivate/" + clientId);

        assertEquals(204, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\""));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\""));

        /*Activate test*/
        RequestSpecification requestPost2 = RestAssured.given();
        Response responsePost2 = requestPost2.post(appUrlClient + "/activate/" + clientId);

        assertEquals(204, responsePost2.getStatusCode());

        responseString = requestGet.get(new URI(appUrlClient)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + clientId + "\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + clientId + "\""));
    }
}
