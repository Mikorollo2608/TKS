package tks.gv.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tks.gv.controllers.ReservationController;
import tks.gv.Court;
import tks.gv.Reservation;
import tks.gv.ui.reservations.ports.AddReservationUseCase;
import tks.gv.ui.reservations.ports.CheckClientReservationBalanceUseCase;
import tks.gv.ui.reservations.ports.DeleteReservationUseCase;
import tks.gv.ui.reservations.ports.GetAllArchiveReservationsUseCase;
import tks.gv.ui.reservations.ports.GetAllClientReservationsUseCase;
import tks.gv.ui.reservations.ports.GetAllCurrentReservationsUseCase;
import tks.gv.ui.reservations.ports.GetClientCurrentReservationsUseCase;
import tks.gv.ui.reservations.ports.GetClientEndedReservationsUseCase;
import tks.gv.ui.reservations.ports.GetCourtCurrentReservationUseCase;
import tks.gv.ui.reservations.ports.GetCourtEndedReservationUseCase;
import tks.gv.ui.reservations.ports.GetReservationByIdUseCase;
import tks.gv.ui.reservations.ports.ReturnCourtUseCase;
import tks.gv.Client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Import(ReservationController.class)
//@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {ReservationController.class})
public class ReservationsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddReservationUseCase addReservationUseCase;

    @MockBean
    private CheckClientReservationBalanceUseCase checkClientReservationBalanceUseCase;

    @MockBean
    private DeleteReservationUseCase deleteReservationUseCase;

    @MockBean
    private GetAllArchiveReservationsUseCase getAllArchiveReservationsUseCase;

    @MockBean
    private GetAllClientReservationsUseCase getAllClientReservationsUseCase;

    @MockBean
    private GetAllCurrentReservationsUseCase getAllCurrentReservationsUseCase;

    @MockBean
    private GetClientCurrentReservationsUseCase getClientCurrentReservationsUseCase;

    @MockBean
    private GetClientEndedReservationsUseCase getClientEndedReservationsUseCase;

    @MockBean
    private GetCourtCurrentReservationUseCase getCourtCurrentReservationUseCase;

    @MockBean
    private GetCourtEndedReservationUseCase getCourtEndedReservationUseCase;

    @MockBean
    private GetReservationByIdUseCase getReservationByIdUseCase;

    @MockBean
    private ReturnCourtUseCase returnCourtUseCase;

    private static Reservation reservation1;
    private static Reservation reservation2;
    private static Reservation reservation3;

    @BeforeAll
    static void init() {
        reservation1 = new Reservation(UUID.randomUUID(),
                new Client(UUID.randomUUID(), "c1", ""),
                new Court(UUID.randomUUID(), 100.0, 100, 1), LocalDateTime.now()
        );
        reservation2 = new Reservation(UUID.randomUUID(),
                new Client(UUID.randomUUID(), "c2", ""),
                new Court(UUID.randomUUID(), 100.0, 100, 2),
                LocalDateTime.now()
        );
        reservation3 = new Reservation(UUID.randomUUID(),
                new Client(UUID.randomUUID(), "c3", ""),
                new Court(UUID.randomUUID(), 100.0, 100, 3),
                LocalDateTime.now()
        );
    }

    @Test
    void getAllCurrentReservationsTest() throws Exception {
        Mockito.when(getAllCurrentReservationsUseCase.getAllCurrentReservations()).thenReturn(List.of(reservation1, reservation2, reservation3));

        mockMvc.perform(MockMvcRequestBuilders.get("/reservations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(reservation1.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(reservation2.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id", Matchers.is(reservation3.getId().toString())));
    }

    @Test
    void getAllCurrentReservationsTestNoCont() throws Exception {
        Mockito.when(getAllCurrentReservationsUseCase.getAllCurrentReservations()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/reservations"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void getAllArchiveReservationsTest() throws Exception {
        Mockito.when(getAllArchiveReservationsUseCase.getAllArchiveReservations()).thenReturn(List.of(reservation1, reservation2, reservation3));

        mockMvc.perform(MockMvcRequestBuilders.get("/reservations/archive"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(reservation1.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(reservation2.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id", Matchers.is(reservation3.getId().toString())));
    }

    @Test
    void getAllArchiveReservationsTestNoCont() throws Exception {
        Mockito.when(getAllArchiveReservationsUseCase.getAllArchiveReservations()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/reservations/archive"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void createReservationTestPos() throws Exception {
//        Mockito.when(addReservationUseCase.addReservation(Mockito.eq("177e7824-ddcc-4b78-86d8-7f4ee09cab70"), Mockito.eq("e9629a08-572e-483a-941f-ece98b30dd0e"), Mockito.eq(LocalDateTime.parse("2023-11-30T17:03:22")))).thenReturn(Mockito.any(Reservation.class));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/reservations/addReservation")
//                        .param("clientId", UUID.fromString("177e7824-ddcc-4b78-86d8-7f4ee09cab70").toString())
//                        .param("courtId", UUID.fromString("e9629a08-572e-483a-941f-ece98b30dd0e").toString())
//                        .param("date", "2023-11-30T17:03:22"))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

//    @Test
//    void createReservationTestNegInvalidData() throws Exception {
//        NewCleaningClassForTests.cleanReservations();
//        newCleaningClassForTests.initClients();
//        newCleaningClassForTests.initCourts();
//        RequestSpecification requestPost = RestAssured.given();
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertTrue(responseString.isEmpty());
//
//        Response responsePost = requestPost.post(appUrlReservation +
//                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted("XXX", NewCleaningClassForTests.court3.getId(),
//                        "2023-11-30T17:03:22"));
//
//        assertEquals(400, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertFalse(responseString.contains("\"beginTime\":\"2023-11-30T17:03:22\""));
//        assertFalse(responseString.contains("\"client\":{\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client3.getId())));
//        assertFalse(responseString.contains("\"court\":{\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court3.getId())));
//    }
//
//    @Test
//    void createReservationTestNegReservedCourt() throws Exception {
//        RequestSpecification requestPost = RestAssured.given();
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertFalse(responseString.contains("\"beginTime\":\"2023-12-15T17:03:22\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client4.getId())));
//        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court2.getId())));
//
//        Response responsePostNeg = requestPost.post(appUrlReservation +
//                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted(NewCleaningClassForTests.client4.getId(), NewCleaningClassForTests.court2.getId(),
//                        "2023-12-15T17:03:22"));
//
//        assertEquals(409, responsePostNeg.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertFalse(responseString.contains("\"beginTime\":\"2023-12-15T17:03:22\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client4.getId())));
//        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court2.getId())));
//    }
//
//    @Test
//    void returnCourtTestPos() throws Exception {
//        RequestSpecification request = RestAssured.given();
//
//        //Check current reservations
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        Response responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        String responseStringArch = responseArch.asString();
//        String[] splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Do return
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePostReturning = requestPost.post(appUrlReservation +
//                "/returnCourt?courtId=%s&date=%s".formatted(NewCleaningClassForTests.court1.getId().toString(), "2023-12-05T17:03:22"));
//
//        assertEquals(204, responsePostReturning.getStatusCode());
//
//        //Check current reservations
//        response = request.get(new URI(appUrlReservation));
//        responseString = response.asString();
//        splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(3, splitedRespStr.length);
//
//        //First Reservation
//        assertFalse(responseString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        responseStringArch = responseArch.asString();
//        splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(4, splitedRespStrArch.length);
//
//        //First Reservation
//        assertTrue(responseStringArch.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
//        assertFalse(responseStringArch.contains("\"endTime\":null"));
//    }
//
//    @Test
//    void returnCourtTestNegInvalidData() throws Exception {
//        RequestSpecification request = RestAssured.given();
//
//        //Check current reservations
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        Response responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        String responseStringArch = responseArch.asString();
//        String[] splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Do return
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePostReturning = requestPost.post(appUrlReservation +
//                "/returnCourt?courtId=%s&date=%s".formatted("XXX", "2023-12-05T17:03:22"));
//
//        assertEquals(400, responsePostReturning.getStatusCode());
//
//        //Check current reservations
//        response = request.get(new URI(appUrlReservation));
//        responseString = response.asString();
//        splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        responseStringArch = responseArch.asString();
//        splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//    }
//
//    @Test
//    void returnCourtTestNegBadUUID() throws Exception {
//        RequestSpecification request = RestAssured.given();
//
//        //Check current reservations
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        Response responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        String responseStringArch = responseArch.asString();
//        String[] splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Do return
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePostReturning = requestPost.post(appUrlReservation +
//                "/returnCourt?courtId=%s&date=%s".formatted(UUID.randomUUID().toString(), "2023-12-05T17:03:22"));
//
//        assertEquals(500, responsePostReturning.getStatusCode());
//
//        //Check current reservations
//        response = request.get(new URI(appUrlReservation));
//        responseString = response.asString();
//        splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        responseStringArch = responseArch.asString();
//        splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
////        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//    }
//
//    @Test
//    void getReservationByIdTest() throws Exception {
//        RequestSpecification request = RestAssured.given();
//
//        //Retrieve UUID
//        Response responseById = request.get(new URI(appUrlReservation + "/" + NewCleaningClassForTests.reservation2.getId()));
//        String responseByIdString = responseById.asString();
//        String[] splitedRespStr = responseByIdString.split("},\\{");
//
//        assertEquals(1, splitedRespStr.length);
//
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client2.getId())));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court2.getId())));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.reservation2.getId())));
//
//        assertEquals(200, responseById.getStatusCode());
//    }
//
//    @Test
//    void getReservationByIdTestNoCont() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/" + UUID.randomUUID()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//
//    @Test
//    void getAllClientReservationsTest() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientReservation?clientId=" + NewCleaningClassForTests.client3.getId()));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(3, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-28T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client3.getId())));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court3.getId())));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"" + NewCleaningClassForTests.reservation3.getId() + "\""));
//
//        //Third Reservation
//        assertTrue(splitedRespStr[2].contains("\"beginTime\":\"2023-12-16T10:00:00\""));
//        assertTrue(splitedRespStr[2].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[2].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client3.getId())));
//        assertTrue(splitedRespStr[2].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[2].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court5.getId())));
//        assertTrue(splitedRespStr[2].contains("\"id\":\"" + NewCleaningClassForTests.reservation7.getId() + "\""));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getAllClientReservationsTestNoCont() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientReservation?clientId=" + NewCleaningClassForTests.client4.getId()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getClientCurrentReservationsTest() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientReservation/current?clientId=" + NewCleaningClassForTests.client1.getId()));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(2, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client1.getId())));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court1.getId())));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
//
//        //Second Reservation
//        assertTrue(splitedRespStr[1].contains("\"beginTime\":\"2023-12-15T10:00:00\""));
//        assertTrue(splitedRespStr[1].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client1.getId())));
//        assertTrue(splitedRespStr[1].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court3.getId())));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"" + NewCleaningClassForTests.reservation6.getId() + "\""));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getClientCurrentReservationsTestNoCont() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientReservation/current?clientId=" + NewCleaningClassForTests.client4.getId()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getClientEndedReservationsTest() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientReservation/ended?clientId=" + NewCleaningClassForTests.client3.getId()));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(2, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-28T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client3.getId())));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court3.getId())));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"" + NewCleaningClassForTests.reservation3.getId() + "\""));
////        assertTrue(splitedRespStr[0].contains("\"endTime\":\"2023-11-30T14:20:00\""));
//
//
//        //Second Reservation
//        assertTrue(splitedRespStr[1].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[1].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client3.getId())));
//        assertTrue(splitedRespStr[1].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court4.getId())));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"" + NewCleaningClassForTests.reservation5.getId() + "\""));
////        assertTrue(splitedRespStr[1].contains("\"endTime\":\"2023-12-01T14:20:00\""));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getClientEndedReservationsTestNoCont() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientReservation/ended?clientId=" + NewCleaningClassForTests.client4.getId()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getCourtCurrentReservationTest() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/courtReservation/current?courtId=" + NewCleaningClassForTests.court2.getId()));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(1, splitedRespStr.length);
//
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client2.getId())));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court2.getId())));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"" + NewCleaningClassForTests.reservation2.getId() + "\""));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getCourtCurrentReservationTestNoCont() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/courtReservation/current?courtId=" + NewCleaningClassForTests.court4.getId()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getCourtEndedReservationTest() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/courtReservation/ended?courtId=" + NewCleaningClassForTests.court3.getId()));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(2, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-28T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client3.getId())));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court3.getId())));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"" + NewCleaningClassForTests.reservation3.getId() + "\""));
////        assertTrue(splitedRespStr[0].contains("\"endTime\":\"2023-11-30T14:20:00\""));
//
//        //Second Reservation
//        assertTrue(splitedRespStr[1].contains("\"beginTime\":\"2023-11-28T15:00:00\""));
//        assertTrue(splitedRespStr[1].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.client2.getId())));
//        assertTrue(splitedRespStr[1].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(NewCleaningClassForTests.court3.getId())));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"" + NewCleaningClassForTests.reservation4.getId() + "\""));
////        assertTrue(splitedRespStr[1].contains("\"endTime\":\"2023-12-02T12:20:00\""));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getCourtEndedReservationTestNoCont() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/courtReservation/ended?courtId=" + NewCleaningClassForTests.court1.getId()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void checkClientReservationBalanceTest() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientBalance?clientId=" + NewCleaningClassForTests.client3.getId()));
//        double balance = Double.parseDouble(response.asString());
//
//        assertTrue(balance > 0);
//    }
//
//    @Test
//    void checkClientReservationBalanceTestZero() throws Exception {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/clientBalance?clientId=" + NewCleaningClassForTests.client4.getId()));
//        double balance = Double.parseDouble(response.asString());
//
//        assertEquals(0, balance);
//    }
//
//    @Test
//    void deleteCourtTestPos() throws Exception {
//        newCleaningClassForTests.initReservations();
//
//        RequestSpecification requestGet = RestAssured.given();
//        RequestSpecification requestDelete = RestAssured.given();
//
//        Response responseGet = requestGet.get(new URI(appUrlReservation));
//        String responseGetString = responseGet.asString();
//
//        assertEquals(4, responseGetString.split("},\\{").length);
//
//        //First Reservation before deleting
//        assertTrue(responseGetString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
//
//        Response responseDelete = requestDelete.delete(new URI(appUrlReservation + "/delete/" + NewCleaningClassForTests.reservation1.getId()));
//
//        responseGet = requestGet.get(new URI(appUrlReservation));
//        responseGetString = responseGet.asString();
//
//        assertEquals(3, responseGetString.split("},\\{").length);
//
//        //First Reservation after deleting
//        assertFalse(responseGetString.contains("\"id\":\"" + NewCleaningClassForTests.reservation1.getId() + "\""));
//
//        assertEquals(204, responseDelete.getStatusCode());
//    }
//
//    @Test
//    void deleteCourtTestNeg() throws Exception {
//        RequestSpecification requestGet = RestAssured.given();
//        RequestSpecification requestDelete = RestAssured.given();
//
//        Response responseGet = requestGet.get(new URI(appUrlReservation + "/archive"));
//        String responseGetString = responseGet.asString();
//
//        assertEquals(3, responseGetString.split("},\\{").length);
//
//        //Third Reservation before deleting
//        assertTrue(responseGetString.contains("\"id\":\"" + NewCleaningClassForTests.reservation3.getId() + "\""));
//
//        Response responseDelete = requestDelete.delete(new URI(appUrlReservation + "/delete/" + NewCleaningClassForTests.reservation3.getId()));
//
//        responseGet = requestGet.get(new URI(appUrlReservation + "/archive"));
//        responseGetString = responseGet.asString();
//
//        assertEquals(3, responseGetString.split("},\\{").length);
//
//        //Third Reservation after deleting
//        assertTrue(responseGetString.contains("\"id\":\"" + NewCleaningClassForTests.reservation3.getId() + "\""));
//
//        assertEquals(409, responseDelete.getStatusCode());
//    }
}
