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
@XmlRootElement(name = SoapConstants.GET_COURT_BY_NUM_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCourtByNumberRequest {

    @XmlElement(name = "courtNumber", required = true)
    private int courtNumber;
}
