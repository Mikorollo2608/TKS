package tks.gv.rentservice.data.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import tks.gv.rentservice.util.SoapConstants;

@Data
@NoArgsConstructor
@XmlRootElement(name = SoapConstants.GET_ALL_COURTS_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAllCourtsRequest {

}
