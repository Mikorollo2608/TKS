package tks.gv.data.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tks.gv.SoapConstants;

@Setter
@Getter
@NoArgsConstructor
@XmlRootElement(name = SoapConstants.COURT_ELEMENT_RESPONSE)
@XmlAccessorType(XmlAccessType.FIELD)
public class CourtSoapResponse {

    @XmlElement(name = "court", required = true)
    private CourtSoap courtSoap;
}
