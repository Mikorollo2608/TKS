package tks.gv.data.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tks.gv.SoapConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = SoapConstants.COURT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"archive", "area", "baseCost", "courtNumber", "id", "rented"})
public class CourtSoap {

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

}
