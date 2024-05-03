package tks.gv.ws;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.RequestCreators;
import org.springframework.ws.test.server.ResponseMatchers;
import org.springframework.xml.transform.StringSource;
import tks.gv.Court;
import tks.gv.endpoints.CourtEndpoint;
import tks.gv.userinterface.courts.ports.AddCourtUseCase;
import tks.gv.userinterface.courts.ports.GetAllCourtsUseCase;
import tks.gv.userinterface.courts.ports.GetCourtByCourtNumberUseCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@WebServiceServerTest(endpoints = {CourtEndpoint.class})
public class CourtEndpointTest {
    private static final Map<String, String> NAMESPACE_MAPPING = createMapping();

    @Autowired
    private MockWebServiceClient webServiceClient;

    @MockBean
    private GetCourtByCourtNumberUseCase getCourtByCourtNumber;
    @MockBean
    private GetAllCourtsUseCase getAllCourts;
    @MockBean
    private AddCourtUseCase addCourt;

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
    void getCourtByCourtNumberTestPositive() throws IOException {
        Mockito.when(getCourtByCourtNumber.getCourtByCourtNumber(court1.getCourtNumber())).thenReturn(court1);

        StringSource request = new StringSource("""
                <tns:getCourtByNumberRequest xmlns:tns="http://data.gv.tks/dto">
                  <tns:courtNumber>1</tns:courtNumber>
                </tns:getCourtByNumberRequest>
                """);

        StringSource expectedResponse = new StringSource(String.format(Locale.US, """
                        <ns2:getCourtByNumberResponse xmlns:ns2="http://data.gv.tks/dto">
                            <ns2:court>
                                <ns2:archive>false</ns2:archive>
                                <ns2:area>%.1f</ns2:area>
                                <ns2:baseCost>%d</ns2:baseCost>
                                <ns2:courtNumber>%d</ns2:courtNumber>
                                <ns2:id>%s</ns2:id>
                                <ns2:rented>false</ns2:rented>
                            </ns2:court>
                        </ns2:getCourtByNumberResponse>
                        """, court1.getArea(), court1.getBaseCost(),
                court1.getCourtNumber(), court1.getId().toString())
        );

        webServiceClient.sendRequest(RequestCreators.withPayload(request))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(new ClassPathResource("schema1.xsd")))
                .andExpect(ResponseMatchers.payload(expectedResponse))
                .andExpect(ResponseMatchers.xpath("/ns2:getCourtByNumberResponse/ns2:court[1]/ns2:id", NAMESPACE_MAPPING)
                        .evaluatesTo(court1.getId().toString()));
    }

    @Test
    void getCourtByCourtNumberTestNoContent() {
        Mockito.when(getCourtByCourtNumber.getCourtByCourtNumber(Mockito.anyInt())).thenReturn(null);

        StringSource request = new StringSource("""
                <tns:getCourtByNumberRequest xmlns:tns="http://data.gv.tks/dto">
                  <tns:courtNumber>1</tns:courtNumber>
                </tns:getCourtByNumberRequest>
                """);

        StringSource expectedResponse = new StringSource("""
                <ns2:getCourtByNumberResponse xmlns:ns2="http://data.gv.tks/dto"/>
                """
        );

        webServiceClient.sendRequest(RequestCreators.withPayload(request))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(expectedResponse));
    }

    @Test
    void getAllCourtsTestPositive() throws IOException {
        Mockito.when(getAllCourts.getAllCourts()).thenReturn(List.of(court1, court2, court3));

        StringSource request = new StringSource("""
                <tns:getAllCourtsRequest xmlns:tns="http://data.gv.tks/dto"/>
                """);

        StringSource expectedResponse = new StringSource(String.format(Locale.US, """
                <ns2:getAllCourtsResponse xmlns:ns2="http://data.gv.tks/dto">
                    <ns2:courts>
                        <ns2:archive>false</ns2:archive>
                        <ns2:area>%.1f</ns2:area>
                        <ns2:baseCost>%d</ns2:baseCost>
                        <ns2:courtNumber>%d</ns2:courtNumber>
                        <ns2:id>%s</ns2:id>
                        <ns2:rented>false</ns2:rented>
                    </ns2:courts>
                    <ns2:courts>
                        <ns2:archive>false</ns2:archive>
                        <ns2:area>%.1f</ns2:area>
                        <ns2:baseCost>%d</ns2:baseCost>
                        <ns2:courtNumber>%d</ns2:courtNumber>
                        <ns2:id>%s</ns2:id>
                        <ns2:rented>false</ns2:rented>
                    </ns2:courts>
                    <ns2:courts>
                        <ns2:archive>false</ns2:archive>
                        <ns2:area>%.1f</ns2:area>
                        <ns2:baseCost>%d</ns2:baseCost>
                        <ns2:courtNumber>%d</ns2:courtNumber>
                        <ns2:id>%s</ns2:id>
                        <ns2:rented>false</ns2:rented>
                    </ns2:courts>
                </ns2:getAllCourtsResponse>
                """, court1.getArea(), court1.getBaseCost(), court1.getCourtNumber(), court1.getId().toString(),
                court2.getArea(), court2.getBaseCost(), court2.getCourtNumber(), court2.getId().toString(),
                court3.getArea(), court3.getBaseCost(), court3.getCourtNumber(), court3.getId().toString())
        );

        webServiceClient.sendRequest(RequestCreators.withPayload(request))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(new ClassPathResource("schema1.xsd")))
                .andExpect(ResponseMatchers.payload(expectedResponse))
                .andExpect(ResponseMatchers.xpath("/ns2:getAllCourtsResponse/ns2:courts[2]/ns2:id", NAMESPACE_MAPPING)
                        .evaluatesTo(court2.getId().toString()));
    }

    @Test
    void getAllCourtsTestNoContent() {
        Mockito.when(getAllCourts.getAllCourts()).thenReturn(new ArrayList<Court>());

        StringSource request = new StringSource("""
                <tns:getAllCourtsRequest xmlns:tns="http://data.gv.tks/dto"/>
                """);

        StringSource expectedResponse = new StringSource("""
                <ns2:getAllCourtsResponse xmlns:ns2="http://data.gv.tks/dto"/>
                """
        );

        webServiceClient.sendRequest(RequestCreators.withPayload(request))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(expectedResponse));
    }

    @Test
    void addCourtTestPositive() throws IOException {
        Court newCourt = new Court(UUID.randomUUID(), 500.0, 500, 5);

        Mockito.when(addCourt.addCourt(Mockito.any(Court.class))).thenReturn(newCourt);

        StringSource request = new StringSource("""
                <tns:addCourtRequest xmlns:tns="http://data.gv.tks/dto">
                  <tns:area>%.1f</tns:area>
                  <tns:baseCost>%d</tns:baseCost>
                  <tns:courtNumber>%d</tns:courtNumber>
                </tns:addCourtRequest>
                """.formatted(newCourt.getArea(), newCourt.getBaseCost(), newCourt.getCourtNumber()));

        StringSource expectedResponse = new StringSource(String.format(Locale.US, """
                <ns2:addCourtResponse xmlns:ns2="http://data.gv.tks/dto">
                    <ns2:created>true</ns2:created>
                    <ns2:court>
                        <ns2:archive>false</ns2:archive>
                        <ns2:area>%.1f</ns2:area>
                        <ns2:baseCost>%d</ns2:baseCost>
                        <ns2:courtNumber>%d</ns2:courtNumber>
                        <ns2:id>%s</ns2:id>
                        <ns2:rented>false</ns2:rented>
                    </ns2:court>
                </ns2:addCourtResponse>
                """, newCourt.getArea(), newCourt.getBaseCost(),
                newCourt.getCourtNumber(), newCourt.getId().toString())
        );

        webServiceClient.sendRequest(RequestCreators.withPayload(request))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(new ClassPathResource("schema1.xsd")))
                .andExpect(ResponseMatchers.payload(expectedResponse))
                .andExpect(ResponseMatchers.xpath("/ns2:addCourtResponse/ns2:court[1]/ns2:id", NAMESPACE_MAPPING)
                        .evaluatesTo(newCourt.getId().toString()))
        ;
    }

    @Test
    void addCourtTestNegative() throws IOException {
        Court newCourt = new Court(UUID.randomUUID(), 500.0, 500, 5);

        Mockito.when(addCourt.addCourt(Mockito.any(Court.class))).thenReturn(null);

        StringSource request = new StringSource("""
                <tns:addCourtRequest xmlns:tns="http://data.gv.tks/dto">
                  <tns:area>%.1f</tns:area>
                  <tns:baseCost>%d</tns:baseCost>
                  <tns:courtNumber>%d</tns:courtNumber>
                </tns:addCourtRequest>
                """.formatted(newCourt.getArea(), newCourt.getBaseCost(), newCourt.getCourtNumber()));

        StringSource expectedResponse = new StringSource("""
                <ns2:addCourtResponse xmlns:ns2="http://data.gv.tks/dto">
                    <ns2:created>false</ns2:created>
                </ns2:addCourtResponse>
                """
        );

        webServiceClient.sendRequest(RequestCreators.withPayload(request))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(new ClassPathResource("schema1.xsd")))
                .andExpect(ResponseMatchers.payload(expectedResponse))
                .andExpect(ResponseMatchers.xpath("/ns2:addCourtResponse/ns2:created[1]", NAMESPACE_MAPPING)
                        .evaluatesTo("false"))
        ;
    }

    private static Map<String, String> createMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("ns2", "http://data.gv.tks/dto");
        return mapping;
    }
}
