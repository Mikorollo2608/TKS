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

import static tks.gv.integrationtests.NewCleaningClassForTests.admin1;
import static tks.gv.integrationtests.NewCleaningClassForTests.admin2;
import static tks.gv.integrationtests.NewCleaningClassForTests.cleanUsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {AppREST.class, NewCleaningClassForTests.class})
@TestPropertySource(locations = {"classpath:application-integrationtest.properties"})
public class AdminControllerTests {

    static final String appUrlAdmins = "http://localhost:8081/api-test/admins";

    @Autowired
    NewCleaningClassForTests newCleaningClassForTests;

    @AfterAll
    static void cleanAtTheEnd() {
        cleanUsers();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        cleanUsers();
        newCleaningClassForTests.initAdmins();
    }

    @Test
    void getAllAdminsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins));
        String responseString = response.asString();

        String[] splitRespStr = responseString.split("},");

        assertEquals(2, splitRespStr.length);

        //First Admin
        assertTrue(splitRespStr[0].contains("\"archive\":false"));
        assertTrue(splitRespStr[0].contains("\"id\":\"%s\"".formatted(admin1.getId())));
        assertTrue(splitRespStr[0].contains("\"login\":\"adminek1@1234\""));

        //Second Admin
        assertTrue(splitRespStr[1].contains("\"archive\":false"));
        assertTrue(splitRespStr[1].contains("\"id\":\"%s\"".formatted(admin2.getId())));
        assertTrue(splitRespStr[1].contains("\"login\":\"adminek2@9876\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllAdminsTestNoCont() throws URISyntaxException {
        cleanUsers();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createAdminTestPos() throws URISyntaxException {
        cleanUsers();
        String JSON = """
                {
                  "login": "johnBravo",
                  "password": "testTO1"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post(appUrlAdmins + "/addAdmin");

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"johnBravo\""));
    }

    @Test
    void createAdminTestNegInvalidData() throws URISyntaxException {
        String json = """
                {
                  "login": " ",
                  "password": "testTO1"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains("\"login\":\" \""));

        Response responsePost = requestPost.post(appUrlAdmins + "/addAdmin");

        assertEquals(400, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains("\"login\":\" \""));
    }

    @Test
    void createAdminTestNegSameLogin() throws URISyntaxException {
        String json = """
                {
                  "login": "adminek1@1234",
                  "password": "testTO1"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"adminek1@1234\""));

        Response responsePost = requestPost.post(appUrlAdmins + "/addAdmin");

        assertEquals(409, responsePost.getStatusCode());
    }

    @Test
    void getAdminByLoginTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/get?login=adminek1@1234"));
        String responseString = response.asString();

        assertTrue(responseString.contains("\"id\":\"%s\",\"login\":\"adminek1@1234\"".formatted(admin1.getId())));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAdminByLoginTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/get?login=564545415612121121"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getAdminByIdTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();

        Response responseById = request.get(new URI(appUrlAdmins + "/" + admin1.getId()));
        String responseByIdString = responseById.asString();

        assertTrue(responseByIdString.contains("\"id\":\"%s\",\"login\":\"adminek1@1234\"".formatted(admin1.getId())));

        assertEquals(200, responseById.getStatusCode());
    }

    @Test
    void getAdminByIdTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/" + UUID.randomUUID()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getAdminByLoginMatchingPos() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/match?login=admin"));
        String responseString = response.asString();

        String[] splitRespStr = responseString.split("},");

        assertEquals(2, splitRespStr.length);

        //First Admin
        assertTrue(splitRespStr[0].contains("\"id\":\"%s\",\"login\":\"adminek1@1234\"".formatted(admin1.getId())));

        //Second Admin
        assertTrue(splitRespStr[1].contains("\"id\":\"%s\",\"login\":\"adminek2@9876\"".formatted(admin2.getId())));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAdminByLoginMatchingNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/match?login=uwu"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());

        assertEquals(204, response.getStatusCode());
    }

    @Test
    void modifyAdminTest() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "fd60c176-d427-4591-ac13-6fb84d904862",
                  "login": "loginekAdm"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"loginekAdm\""));

        Response responsePut = requestPut.put(appUrlAdmins + "/modifyAdmin");

        assertEquals(204, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek1@1234\""));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"loginekAdm\""));
    }

    @Test
    void modifyAdminTestNegInvalidData() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "fd60c176-d427-4591-ac13-6fb84d904862",
                  "login": " "
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\" \""));

        Response responsePut = requestPut.put(appUrlAdmins + "/modifyAdmin");

        assertEquals(400, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\" \""));
    }

    @Test
    void modifyAdminTestNegRepeatLoginOfAnotherAdmin() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "fd60c176-d427-4591-ac13-6fb84d904862",
                  "login": "adminek2@9876"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"adminek2@9876\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek2@9876\""));

        Response responsePut = requestPut.put(appUrlAdmins + "/modifyAdmin");

        assertEquals(409, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"adminek2@9876\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\"," +
                "\"login\":\"adminek2@9876\""));
    }

    @Test
    void archiveAndActivateAdminTest() throws URISyntaxException {
        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        /*Archive test*/
        assertTrue(responseString.contains(
            "\"archive\":false," +
            "\"id\":\"" + admin1.getId() + "\""));
        assertFalse(responseString.contains(
            "\"archive\":true," +
            "\"id\":\"" + admin1.getId() + "\""));

        RequestSpecification requestPost = RestAssured.given();
        Response responsePost = requestPost.post(appUrlAdmins + "/deactivate/" + admin1.getId());

        assertEquals(204, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\""));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\""));

        /*Activate test*/
        RequestSpecification requestPost2 = RestAssured.given();
        Response responsePost2 = requestPost2.post(appUrlAdmins + "/activate/" + admin1.getId());

        assertEquals(204, responsePost2.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + admin1.getId() + "\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + admin1.getId() + "\""));
    }
}
