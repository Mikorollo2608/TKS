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
@XmlRootElement(name = SoapConstants.ADD_COURT_RES)
@XmlAccessorType(XmlAccessType.FIELD)
public class AddCourtResponse {

    @XmlElement(required = true)
    private boolean created;

    @XmlElement(name = "court")
    private CourtSoap courtSoap;
}
