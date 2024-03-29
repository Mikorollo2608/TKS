package tks.gv.data.dto;

import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tks.gv.SoapConstants;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = SoapConstants.COURT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"archive", "area", "baseCost", "courtNumber", "id", "rented"})
public class CourtXmlDTORes {

    @XmlElement(name = "id")
    private String id;
    @XmlElement(name = "area")
    private double area;
    @XmlElement(name = "baseCost")
    private int baseCost;
    @XmlElement(name = "courtNumber")
    private int courtNumber;
    @XmlElement(name = "archive")
    private boolean archive;
    @XmlElement(name = "rented")
    private boolean rented;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtXmlDTORes courtXmlDTO = (CourtXmlDTORes) o;
        return Objects.equal(id, courtXmlDTO.id) &&
                Double.compare(area, courtXmlDTO.area) == 0 &&
                baseCost == courtXmlDTO.baseCost &&
                courtNumber == courtXmlDTO.courtNumber &&
                archive == courtXmlDTO.archive &&
                rented == courtXmlDTO.rented;
    }
}
