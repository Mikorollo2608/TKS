package tks.gv.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tks.gv.controllers.courts.CourtController;
import tks.gv.courts.Court;
import tks.gv.exceptions.CourtException;
import tks.gv.exceptions.CourtNumberException;
import tks.gv.userinterface.courts.ports.ActivateCourtUseCase;
import tks.gv.userinterface.courts.ports.AddCourtUseCase;
import tks.gv.userinterface.courts.ports.DeactivateUseCase;
import tks.gv.userinterface.courts.ports.DeleteCourtUseCase;
import tks.gv.userinterface.courts.ports.GetAllCourtsUseCase;
import tks.gv.userinterface.courts.ports.GetCourtByCourtNumberUseCase;
import tks.gv.userinterface.courts.ports.GetCourtByIdUseCase;
import tks.gv.userinterface.courts.ports.ModifyCourtUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Import(CourtController.class)
//@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {CourtController.class})
public class CourtControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivateCourtUseCase activateCourt;

    @MockBean
    private AddCourtUseCase addCourt;

    @MockBean
    private DeactivateUseCase deactivateCourt;

    @MockBean
    private DeleteCourtUseCase deleteCourt;

    @MockBean
    private GetAllCourtsUseCase getAllCourts;

    @MockBean
    private GetCourtByIdUseCase getCourtById;

    @MockBean
    private GetCourtByCourtNumberUseCase getCourtByCourtNumber;

    @MockBean
    private ModifyCourtUseCase modifyCourt;

    private static Court court1;
    private static Court court2;
    private static Court court3;

    @BeforeAll
    static void init() {
        court1 = new Court(UUID.randomUUID(), 100.0, 100, 1);
        court2 = new Court(UUID.randomUUID(), 200.0, 200, 2);
        court3 = new Court(UUID.randomUUID(), 300.0, 300, 3);
    }

    @Test
    void getAllCourtsTest() throws Exception {
        Mockito.when(getAllCourts.getAllCourts()).thenReturn(List.of(court1, court2, court3));

        mockMvc.perform(MockMvcRequestBuilders.get("/courts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courtNumber", Matchers.is(court1.getCourtNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].courtNumber", Matchers.is(court2.getCourtNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].courtNumber", Matchers.is(court3.getCourtNumber())));

    }

    @Test
    void getAllCourtsTestNoCont() throws Exception {
        Mockito.when(getAllCourts.getAllCourts()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/courts"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void createCourtTestPos() throws Exception {
        Mockito.when(addCourt.addCourt(Mockito.any(Court.class))).thenReturn(Mockito.any(Court.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/courts/addCourt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "area": 120.0,
                                    "baseCost": 50,
                                    "courtNumber": 15
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void createCourtTestNegInvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/courts/addCourt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "area": 120.0,
                                    "baseCost": -50,
                                    "courtNumber": 15
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("[must be greater than or equal to 0]"));
    }

    @Test
    void createCourtTestNegSameNumber() throws Exception {
        Mockito.when(addCourt.addCourt(Mockito.any(Court.class))).thenThrow(new CourtNumberException("bad court number"));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/courts/addCourt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "area": 120.0,
                                  "baseCost": 50,
                                  "courtNumber": 2
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("bad court number"));
    }

    @Test
    void getCourtByCourtNumberTest() throws Exception {
        Mockito.when(getCourtByCourtNumber.getCourtByCourtNumber(court1.getCourtNumber())).thenReturn(court1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/courts/get")
                        .param("number", "" + court1.getCourtNumber()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(court1.getId().toString())));
    }

    @Test
    void getCourtByCourtNumberTestNoCont() throws Exception {
        Mockito.when(getCourtByCourtNumber.getCourtByCourtNumber(court1.getCourtNumber())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/courts/get")
                        .param("number", "" + court1.getCourtNumber()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void getCourtByIdTest() throws Exception {
        Mockito.when(getCourtById.getCourtById(court1.getId())).thenReturn(court1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/courts/{id}", court1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.courtNumber", Matchers.is(court1.getCourtNumber())));
    }

    @Test
    void getCourtByIdTestNoCont() throws Exception {
        Mockito.when(getCourtById.getCourtById(court1.getId())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/courts/{id}", court1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void modifyCourtTest() throws Exception {
        Mockito.doNothing().when(modifyCourt).modifyCourt(Mockito.any(Court.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/courts/modifyCourt/{id}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "area": 150.0,
                                  "baseCost": 75,
                                  "courtNumber": 2
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(modifyCourt, Mockito.times(1)).modifyCourt(Mockito.any(Court.class));
    }

    @Test
    void modifyCourtTestNegInvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/courts/modifyCourt/{id}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "area": 150.0,
                                  "baseCost": -75,
                                  "courtNumber": 2
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("[must be greater than or equal to 0]"));
    }

    @Test
    void modifyCourtTestNegRepeatNumberOfAnotherCourt() throws Exception {
        Mockito.doThrow(new CourtNumberException("bad court number")).when(modifyCourt).modifyCourt(Mockito.any(Court.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/courts/modifyCourt/{id}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "area": 120.0,
                                  "baseCost": 50,
                                  "courtNumber": 2
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("bad court number"));

        Mockito.verify(modifyCourt, Mockito.times(1)).modifyCourt(Mockito.any(Court.class));
    }

    @Test
    void activateCourtTest() throws Exception {
        Mockito.doNothing().when(activateCourt).activateCourt(court1.getId());
        mockMvc.perform(MockMvcRequestBuilders.post("/courts/activate/{id}", court1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(activateCourt, Mockito.times(1)).activateCourt(court1.getId());
    }

    @Test
    void archiveCourtTest() throws Exception {
        Mockito.doNothing().when(deactivateCourt).deactivateCourt(court1.getId());
        mockMvc.perform(MockMvcRequestBuilders.post("/courts/deactivate/{id}", court1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(deactivateCourt, Mockito.times(1)).deactivateCourt(court1.getId());
    }

    @Test
    void deleteCourtTestPos() throws Exception {
        Mockito.doNothing().when(deleteCourt).deleteCourt(court1.getId());
        mockMvc.perform(MockMvcRequestBuilders.delete("/courts/delete/{id}", court1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(deleteCourt, Mockito.times(1)).deleteCourt(court1.getId());
    }

    @Test
    void deleteCourtTestNeg() throws Exception {
        Mockito.doThrow(new CourtException("bad bad")).when(deleteCourt).deleteCourt(court1.getId());
        mockMvc.perform(MockMvcRequestBuilders.delete("/courts/delete/{id}", court1.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("bad bad"));

        Mockito.verify(deleteCourt, Mockito.times(1)).deleteCourt(court1.getId());
    }
}
