package tks.gv.rentservice.data.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import tks.gv.rentservice.util.SoapConstants;

@Data
@NoArgsConstructor
@XmlRootElement(name = SoapConstants.ADD_COURT_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class AddCourtRequest {

    @XmlElement(name = "area", required = true)
    private double area;
    @XmlElement(name = "baseCost", required = true)
    private int baseCost;
    @XmlElement(name = "courtNumber", required = true)
    private int courtNumber;
}
