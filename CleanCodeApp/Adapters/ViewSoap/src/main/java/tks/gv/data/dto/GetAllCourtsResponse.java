package tks.gv.data.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import tks.gv.util.SoapConstants;

import java.util.List;

@Data
@NoArgsConstructor
@XmlRootElement(name = SoapConstants.GET_ALL_COURTS_RES)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAllCourtsResponse {

    @XmlElement(name = "courts", required = true)
    private List<CourtSoap> courtsSoap;
}
