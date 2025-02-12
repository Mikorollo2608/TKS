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

import static tks.gv.integrationtests.NewCleaningClassForTests.adminRes1;
import static tks.gv.integrationtests.NewCleaningClassForTests.adminRes2;
import static tks.gv.integrationtests.NewCleaningClassForTests.cleanUsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {AppREST.class, NewCleaningClassForTests.class})
@TestPropertySource(locations = {"classpath:application-integrationtest.properties"})
public class ResourceAdminControllerTests {

    static final String appUrlResAdmins = "http://localhost:8081/api-test/resAdmins";

    @Autowired
    NewCleaningClassForTests newCleaningClassForTests;

    @AfterAll
    static void cleanAtTheEnd() {
        cleanUsers();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        cleanUsers();
        newCleaningClassForTests.initResAdmins();
    }

    @Test
    void getAllResAdminsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlResAdmins));
        String responseString = response.asString();

        String[] splitRespStr = responseString.split("},");

        assertEquals(2, splitRespStr.length);

        //First Admin
        assertTrue(splitRespStr[0].contains("\"archive\":false"));
        assertTrue(splitRespStr[0].contains("\"id\":\"%s\"".formatted(adminRes1.getId())));
        assertTrue(splitRespStr[0].contains("\"login\":\"adminekRes1@1234\""));

        //Second Admin
        assertTrue(splitRespStr[1].contains("\"archive\":false"));
        assertTrue(splitRespStr[1].contains("\"id\":\"%s\"".formatted(adminRes2.getId())));
        assertTrue(splitRespStr[1].contains("\"login\":\"adminekRes2@9876\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllAdminsTestNoCont() throws URISyntaxException {
        cleanUsers();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlResAdmins));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createAdminTestPos() throws URISyntaxException {
        cleanUsers();
        String JSON = """
                {
                  "login": "johnBravo2",
                  "password": "testTO1"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post(appUrlResAdmins + "/addResAdmin");

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"johnBravo2\""));
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
        String responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertFalse(responseString.contains("\"login\":\" \""));

        Response responsePost = requestPost.post(appUrlResAdmins + "/addResAdmin");

        assertEquals(400, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertFalse(responseString.contains("\"login\":\" \""));
    }

    @Test
    void createAdminTestNegSameLogin() throws URISyntaxException {
        String json = """
                {
                  "login": "adminekRes1@1234",
                  "password": "testTO1"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"adminekRes1@1234\""));

        Response responsePost = requestPost.post(appUrlResAdmins + "/addResAdmin");

        assertEquals(409, responsePost.getStatusCode());
    }

    @Test
    void getAdminByLoginTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlResAdmins + "/get?login=adminekRes1@1234"));
        String responseString = response.asString();

        assertTrue(responseString.contains("\"id\":\"%s\",\"login\":\"adminekRes1@1234\"".formatted(adminRes1.getId())));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAdminByLoginTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlResAdmins + "/get?login=564545415612121121"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getAdminByIdTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();

        Response responseById = request.get(new URI(appUrlResAdmins + "/" + adminRes1.getId()));
        String responseByIdString = responseById.asString();

        assertTrue(responseByIdString.contains("\"id\":\"%s\",\"login\":\"adminekRes1@1234\"".formatted(adminRes1.getId())));

        assertEquals(200, responseById.getStatusCode());
    }

    @Test
    void getAdminByIdTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlResAdmins + "/" + UUID.randomUUID()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getAdminByLoginMatchingPos() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlResAdmins + "/match?login=admin"));
        String responseString = response.asString();

        String[] splitRespStr = responseString.split("},");

        assertEquals(2, splitRespStr.length);

        //First Admin
        assertTrue(splitRespStr[0].contains("\"id\":\"%s\",\"login\":\"adminekRes1@1234\"".formatted(adminRes1.getId())));

        //Second Admin
        assertTrue(splitRespStr[1].contains("\"id\":\"%s\",\"login\":\"adminekRes2@9876\"".formatted(adminRes2.getId())));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAdminByLoginMatchingNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlResAdmins + "/match?login=uwu"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());

        assertEquals(204, response.getStatusCode());
    }

    @Test
    void modifyAdminTest() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "0c5f74c8-5a7e-4809-a6d3-bed663083b07",
                  "login": "loginekAdm"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"loginekAdm\""));

        Response responsePut = requestPut.put(appUrlResAdmins + "/modifyResAdmin");

        assertEquals(204, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes1@1234\""));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"loginekAdm\""));
    }

    @Test
    void modifyAdminTestNegInvalidData() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "0c5f74c8-5a7e-4809-a6d3-bed663083b07",
                  "login": " "
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\" \""));

        Response responsePut = requestPut.put(appUrlResAdmins + "/modifyResAdmin");

        assertEquals(400, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\" \""));
    }

    @Test
    void modifyAdminTestNegRepeatLoginOfAnotherAdmin() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "id": "0c5f74c8-5a7e-4809-a6d3-bed663083b07",
                  "login": "adminekRes2@9876"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"adminekRes2@9876\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes2@9876\""));

        Response responsePut = requestPut.put(appUrlResAdmins + "/modifyResAdmin");

        assertEquals(409, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"adminekRes2@9876\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes1@1234\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\"," +
                "\"login\":\"adminekRes2@9876\""));
    }

    @Test
    void archiveAndActivateAdminTest() throws URISyntaxException {
        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        /*Archive test*/
        assertTrue(responseString.contains(
            "\"archive\":false," +
            "\"id\":\"" + adminRes1.getId() + "\""));
        assertFalse(responseString.contains(
            "\"archive\":true," +
            "\"id\":\"" + adminRes1.getId() + "\""));

        RequestSpecification requestPost = RestAssured.given();
        Response responsePost = requestPost.post(appUrlResAdmins + "/deactivate/" + adminRes1.getId());

        assertEquals(204, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\""));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\""));

        /*Activate test*/
        RequestSpecification requestPost2 = RestAssured.given();
        Response responsePost2 = requestPost2.post(appUrlResAdmins + "/activate/" + adminRes1.getId());

        assertEquals(204, responsePost2.getStatusCode());

        responseString = requestGet.get(new URI(appUrlResAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + adminRes1.getId() + "\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + adminRes1.getId() + "\""));
    }
}
