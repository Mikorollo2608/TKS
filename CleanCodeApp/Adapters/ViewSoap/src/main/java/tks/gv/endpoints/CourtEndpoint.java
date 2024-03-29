package tks.gv.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import tks.gv.SoapConstants;
import tks.gv.data.dto.CourtXmlDTORes;
import tks.gv.data.mappers.dto.CourtMapperXml;
import tks.gv.infrastructure.courts.ports.GetCourtByCourtNumberPort;

@Endpoint
public class CourtEndpoint {

    private final GetCourtByCourtNumberPort getCourtByCourtNumberPort;

    @Autowired
    public CourtEndpoint(GetCourtByCourtNumberPort getCourtByCourtNumberPort) {
        this.getCourtByCourtNumberPort = getCourtByCourtNumberPort;
    }

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = SoapConstants.COURT_ELEMENT_NAME)
    @ResponsePayload
    public CourtXmlDTORes getCourtByCourtNumber(@RequestPayload CourtXmlDTORes request) {
        return CourtMapperXml.toXmlCourt(
                getCourtByCourtNumberPort.getCourtByCourtNumber(request.getCourtNumber())
        );
    }
}