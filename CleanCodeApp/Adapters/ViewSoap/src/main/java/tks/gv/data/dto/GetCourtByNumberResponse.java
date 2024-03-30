package tks.gv.data.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import tks.gv.SoapConstants;

@Data
@NoArgsConstructor
@XmlRootElement(name = SoapConstants.COURT_ELEMENT_RESPONSE)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCourtByNumberResponse {

    @XmlElement(name = "court", required = true)
    private CourtSoap courtSoap;
}
