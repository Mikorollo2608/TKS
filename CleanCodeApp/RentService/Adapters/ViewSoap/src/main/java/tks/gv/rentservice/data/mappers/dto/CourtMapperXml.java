package tks.gv.rentservice.data.mappers.dto;

import tks.gv.rentservice.Court;
import tks.gv.rentservice.data.dto.CourtSoap;

import java.util.UUID;

public class CourtMapperXml {
    public static CourtSoap toXmlCourt(Court court) {
        if (court == null) return null;
        return new CourtSoap(
                court.getId().toString(),
                court.getArea(),
                court.getBaseCost(),
                court.getCourtNumber(),
                court.isArchive(),
                court.isRented()
        );
    }

    public static Court fromXmlCourt(CourtSoap courtXmlDTO) {
        Court court = new Court(courtXmlDTO.getId() != null ? UUID.fromString(courtXmlDTO.getId()) : null,
                courtXmlDTO.getArea(),
                courtXmlDTO.getBaseCost(),
                courtXmlDTO.getBaseCost()
        );
        court.setArchive(courtXmlDTO.isArchive());
        court.setRented(courtXmlDTO.isRented());

        return court;
    }
}
