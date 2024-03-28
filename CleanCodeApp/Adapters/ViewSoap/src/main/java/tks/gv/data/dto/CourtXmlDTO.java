package tks.gv.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "area", "baseCost", "courtNumber", "id", "rented"})
public class CourtXmlDTO {

    @JsonProperty("id")
    private String id;
    @JsonProperty("area")
    private double area;
    @JsonProperty("baseCost")
    private int baseCost;
    @JsonProperty("courtNumber")
    private int courtNumber;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty("rented")
    private boolean rented;

    public CourtXmlDTO(@JsonProperty("id") String id,
                       @JsonProperty("area") double area,
                       @JsonProperty("baseCost") int baseCost,
                       @JsonProperty("courtNumber") int courtNumber,
                       @JsonProperty("archive") boolean archive,
                       @JsonProperty("rented") boolean rented) {
        this.id = id;
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
        this.archive = archive;
        this.rented = rented;
    }

    @Override
    public boolean equals(Object o) {
        ///TODO
        return false;
    }
}
