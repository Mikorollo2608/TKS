package tks.gv.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import tks.gv.Court;
import tks.gv.data.dto.AddCourtRequest;
import tks.gv.data.dto.AddCourtResponse;
import tks.gv.data.dto.GetAllCourtsRequest;
import tks.gv.data.dto.GetAllCourtsResponse;
import tks.gv.userinterface.courts.ports.AddCourtUseCase;
import tks.gv.userinterface.courts.ports.GetAllCourtsUseCase;
import tks.gv.util.SoapConstants;
import tks.gv.data.dto.GetCourtByNumberRequest;
import tks.gv.data.dto.GetCourtByNumberResponse;
import tks.gv.data.mappers.dto.CourtMapperXml;
import tks.gv.userinterface.courts.ports.GetCourtByCourtNumberUseCase;

@Endpoint
public class CourtEndpoint {

    private final GetCourtByCourtNumberUseCase getCourtByCourtNumber;
    private final GetAllCourtsUseCase getAllCourts;
    private final AddCourtUseCase addCourt;

    @Autowired
    public CourtEndpoint(GetCourtByCourtNumberUseCase getCourtByCourtNumber, GetAllCourtsUseCase getAllCourts, AddCourtUseCase addCourt) {
        this.getCourtByCourtNumber = getCourtByCourtNumber;
        this.getAllCourts = getAllCourts;
        this.addCourt = addCourt;
    }

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = SoapConstants.GET_COURT_BY_NUM_REQ)
    @ResponsePayload
    public GetCourtByNumberResponse getCourtByCourtNumber(@RequestPayload GetCourtByNumberRequest request) {
        GetCourtByNumberResponse response = new GetCourtByNumberResponse();
        response.setCourtSoap(
                CourtMapperXml.toXmlCourt(getCourtByCourtNumber.getCourtByCourtNumber(request.getCourtNumber()))
        );

        return response;
    }

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = SoapConstants.GET_ALL_COURTS_REQ)
    @ResponsePayload
    public GetAllCourtsResponse getAllCourts(@RequestPayload GetAllCourtsRequest request) {
        GetAllCourtsResponse response = new GetAllCourtsResponse();
        response.setCourtsSoap(
                getAllCourts.getAllCourts()
                        .stream()
                        .map(CourtMapperXml::toXmlCourt)
                        .toList()
        );

        return response;
    }

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = SoapConstants.ADD_COURT_REQ)
    @ResponsePayload
    public AddCourtResponse addCourt(@RequestPayload AddCourtRequest request) {
        AddCourtResponse response = new AddCourtResponse();
        Court newCourt = addCourt.addCourt(
                new Court(null, request.getArea(), request.getBaseCost(), request.getCourtNumber())
        );

        if (newCourt != null) {
            response.setCreated(true);
            response.setCourtSoap(CourtMapperXml.toXmlCourt(newCourt));
        } else {
            response.setCreated(false);
        }

        return response;
    }
}