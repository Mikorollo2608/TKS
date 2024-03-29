package tks.gv.data.mappers.dto;

import tks.gv.courts.Court;
import tks.gv.data.dto.CourtXmlDTORes;

import java.util.UUID;

public class CourtMapperXml {
    public static CourtXmlDTORes toXmlCourt(Court court) {
        if (court == null) return null;
        return new CourtXmlDTORes(
                court.getId().toString(),
                court.getArea(),
                court.getBaseCost(),
                court.getCourtNumber(),
                court.isArchive(),
                court.isRented()
        );
    }

    public static Court fromXmlCourt(CourtXmlDTORes courtXmlDTO) {
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
