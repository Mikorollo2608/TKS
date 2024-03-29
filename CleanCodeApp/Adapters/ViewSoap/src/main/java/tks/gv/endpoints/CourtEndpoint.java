package tks.gv.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import tks.gv.data.dto.CourtXmlDTORes;
import tks.gv.data.mappers.dto.CourtMapperXml;
import tks.gv.infrastructure.courts.ports.GetCourtByCourtNumberPort;

@Endpoint
public class CourtEndpoint {
    private static final String NAMESPACE_URI = "http://data.gv.tks/dto";

    private final GetCourtByCourtNumberPort getCourtByCourtNumberPort;

    @Autowired
    public CourtEndpoint(GetCourtByCourtNumberPort getCourtByCourtNumberPort) {
        this.getCourtByCourtNumberPort = getCourtByCourtNumberPort;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "court")
    @ResponsePayload
    public CourtXmlDTORes getCourtById(@RequestPayload CourtXmlDTORes request) {
        CourtXmlDTORes response = CourtMapperXml.toXmlCourt(
                getCourtByCourtNumberPort.getCourtByCourtNumber(request.getCourtNumber())
        );

        return response;
    }
}