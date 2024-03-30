package tks.gv.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import tks.gv.SoapConstants;
import tks.gv.data.dto.GetCourtByNumberRequest;
import tks.gv.data.dto.GetCourtByNumberResponse;
import tks.gv.data.mappers.dto.CourtMapperXml;
import tks.gv.infrastructure.courts.ports.GetCourtByCourtNumberPort;

@Endpoint
public class CourtEndpoint {

    private final GetCourtByCourtNumberPort getCourtByCourtNumberPort;

    @Autowired
    public CourtEndpoint(GetCourtByCourtNumberPort getCourtByCourtNumberPort) {
        this.getCourtByCourtNumberPort = getCourtByCourtNumberPort;
    }

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = SoapConstants.COURT_ELEMENT_REQUEST)
    @ResponsePayload
    public GetCourtByNumberResponse getCourtByCourtNumber(@RequestPayload GetCourtByNumberRequest request) {
        GetCourtByNumberResponse response = new GetCourtByNumberResponse();
        response.setCourtSoap(
                CourtMapperXml.toXmlCourt(getCourtByCourtNumberPort.getCourtByCourtNumber(request.getCourtNumber()))
        );

        return response;
    }
}