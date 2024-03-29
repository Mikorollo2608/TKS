package tks.gv.data.dto;

import com.google.common.base.Objects;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "court")
@XmlType(propOrder = {"archive", "area", "baseCost", "courtNumber", "id", "rented"})
public class CourtXmlDTO {

    private String id;
    private double area;
    private int baseCost;
    private int courtNumber;
    private boolean archive;
    private boolean rented;

    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    @XmlElement(name = "area")
    public double getArea() {
        return area;
    }

    @XmlElement(name = "baseCost")
    public int getBaseCost() {
        return baseCost;
    }

    @XmlElement(name = "courtNumber")
    public int getCourtNumber() {
        return courtNumber;
    }

    @XmlElement(name = "archive")
    public boolean isArchive() {
        return archive;
    }

    @XmlElement(name = "rented")
    public boolean isRented() {
        return rented;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtXmlDTO courtXmlDTO = (CourtXmlDTO) o;
        return Objects.equal(id, courtXmlDTO.id) &&
                Double.compare(area, courtXmlDTO.area) == 0 &&
                baseCost == courtXmlDTO.baseCost &&
                courtNumber == courtXmlDTO.courtNumber &&
                archive == courtXmlDTO.archive &&
                rented == courtXmlDTO.rented;
    }
}
