package tks.gv.data.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import tks.gv.util.SoapConstants;

@Data
@NoArgsConstructor
@XmlRootElement(name = SoapConstants.GET_COURT_BY_NUMBER_RES)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCourtByNumberResponse {

    @XmlElement(name = "court", required = true)
    private CourtSoap courtSoap;
}
