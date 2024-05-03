package tks.gv.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tks.gv.controllers.ClientController;
import tks.gv.exceptions.ClientLoginException;
import tks.gv.ui.clients.ports.ChangeClientStatusUseCase;
import tks.gv.ui.clients.ports.GetAllClientsUseCase;
import tks.gv.ui.clients.ports.GetClientByIdUseCase;
import tks.gv.ui.clients.ports.GetClientByLoginUseCase;
import tks.gv.ui.clients.ports.ModifyClientUseCase;
import tks.gv.ui.clients.ports.RegisterClientUseCase;
import tks.gv.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Import(ClientController.class)
//@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {ClientController.class})
public class ClientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterClientUseCase registerClientUseCase;

    @MockBean
    private GetAllClientsUseCase getAllClientsUseCase;

    @MockBean
    private GetClientByIdUseCase getClientByIdUseCase;

    @MockBean
    private GetClientByLoginUseCase getClientByLoginUseCase;

    @MockBean
    private ModifyClientUseCase modifyClientUseCase;

    @MockBean
    private ChangeClientStatusUseCase changeClientStatusUseCase;


    private static Client client1;
    private static Client client2;
    private static Client client3;

    @BeforeAll
    static void init() {
        client1 = new Client(UUID.randomUUID(),"loginek", "normal");
        client2 = new Client(UUID.randomUUID(), "loginek13", "athlete");
        client3 = new Client(UUID.randomUUID(),"michas13", "coach");
    }

    @Test
    void getAllClientsTest() throws Exception {
        Mockito.when(getAllClientsUseCase.getAllClients()).thenReturn(List.of(client1, client2, client3));

        mockMvc.perform(MockMvcRequestBuilders.get("/clients"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].login", Matchers.is(client1.getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].login", Matchers.is(client2.getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].login", Matchers.is(client3.getLogin())));
    }

    @Test
    void getAllClientsTestNoCont() throws Exception {
        Mockito.when(getAllClientsUseCase.getAllClients()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/clients"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void createClientTestPos() throws Exception {
        Mockito.when(registerClientUseCase.registerClient(Mockito.any(Client.class))).thenReturn(Mockito.any(Client.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/clients/addClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "michas13",
                                  "clientTypeName": "normal"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void createClientTestNegInvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/clients/addClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": " ",
                                  "clientTypeName": "normal"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("[must not be blank]"));
    }

    @Test
    void createClientTestNegSameLogin() throws Exception {
        Mockito.when(registerClientUseCase.registerClient(Mockito.any(Client.class))).thenThrow(new ClientLoginException("bad login"));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/clients/addClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "michaS13",
                                  "clientTypeName": "normal"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("bad login"));
    }

    @Test
    void getClientByLoginTest() throws Exception {
        Mockito.when(getClientByLoginUseCase.getClientByLogin(client1.getLogin())).thenReturn(client1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clients/get")
                        .param("login", client1.getLogin()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(client1.getId().toString())));
    }

    @Test
    void getClientByLoginTestNoCont() throws Exception {
        Mockito.when(getClientByLoginUseCase.getClientByLogin(client1.getLogin())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clients/get")
                        .param("login", client1.getLogin()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void getClientByIdTest() throws Exception {
        Mockito.when(getClientByIdUseCase.getClientById(client1.getId().toString())).thenReturn(client1);

        mockMvc.perform(MockMvcRequestBuilders.get("/clients/{id}", client1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", Matchers.is(client1.getLogin())));
    }

    @Test
    void getClientByIdTestNoCont() throws Exception {
        Mockito.when(getClientByIdUseCase.getClientById(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/clients/{id}", client1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void getClientByLoginMatchingPos() throws Exception {
        Mockito.when(getClientByLoginUseCase.getClientByLoginMatching("log")).thenReturn(List.of(client1, client2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clients/match")
                        .param("login", "log"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].login", Matchers.is(client1.getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].login", Matchers.is(client2.getLogin())));
    }

    @Test
    void getClientByLoginMatchingNoCont() throws Exception {
        Mockito.when(getClientByLoginUseCase.getClientByLoginMatching("log")).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/clients/match")
                        .param("login", "log"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void modifyClientTest() throws Exception {
        Mockito.doNothing().when(modifyClientUseCase).modifyClient(Mockito.any(Client.class));
        mockMvc.perform(MockMvcRequestBuilders
                .put("/clients/modifyClient")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "archive": false,
                          "id": "8d83bbda-e38a-4cf2-9136-40e5310c5761",
                          "login": "loginek",
                          "clientTypeName": "coach"
                        }
                        """))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(modifyClientUseCase, Mockito.times(1)).modifyClient(Mockito.any(Client.class));
    }

    @Test
    void modifyClientTestNegInvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/clients/modifyClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "archive": false,
                          "id": "8d83bbda-e38a-4cf2-9136-40e5310c5761",
                          "login": "  ",
                          "clientTypeName": "normal"
                        }
                        """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("[must not be blank]"));
    }

    @Test
    void modifyClientTestNegRepeatLoginOfAnotherClient() throws Exception {
        Mockito.doThrow(new ClientLoginException("bad login")).when(modifyClientUseCase).modifyClient(Mockito.any(Client.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/clients/modifyClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "archive": false,
                                  "id": "8d83bbda-e38a-4cf2-9136-40e5310c5761",
                                  "login": "loginek",
                                  "clientTypeName": "coach"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("bad login"));
    }

    @Test
    void activateClientTest() throws Exception {
        Mockito.doNothing().when(changeClientStatusUseCase).activateClient(client1.getId().toString());
        mockMvc.perform(MockMvcRequestBuilders.post("/clients/activate/{id}", client1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(changeClientStatusUseCase, Mockito.times(1)).activateClient(client1.getId().toString());
    }

    @Test
    void archiveClientTest() throws Exception {
        Mockito.doNothing().when(changeClientStatusUseCase).deactivateClient(client1.getId().toString());
        mockMvc.perform(MockMvcRequestBuilders.post("/clients/deactivate/{id}", client1.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(changeClientStatusUseCase, Mockito.times(1)).deactivateClient(client1.getId().toString());
    }
}
