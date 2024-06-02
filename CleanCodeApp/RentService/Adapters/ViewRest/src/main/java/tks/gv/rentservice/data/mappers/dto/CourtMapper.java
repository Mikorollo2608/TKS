package tks.gv.rentservice.data.mappers.dto;
import tks.gv.rentservice.Court;
import tks.gv.rentservice.data.dto.CourtDTO;

import java.util.UUID;

public class CourtMapper {
    public static CourtDTO toJsonCourt(Court court) {
        if (court == null) return null;
        return new CourtDTO(court.getId().toString(),
                court.getArea(),
                court.getBaseCost(),
                court.getCourtNumber(),
                court.isArchive(),
                court.isRented());
    }

    public static Court fromJsonCourt(CourtDTO courtMapper) {
        Court courtModel = new Court(courtMapper.getId() != null ? UUID.fromString(courtMapper.getId()) : null,
                courtMapper.getArea(),
                courtMapper.getBaseCost(),
                courtMapper.getCourtNumber());
        courtModel.setArchive(courtMapper.isArchive());
        courtModel.setRented(courtMapper.isRented());
        return courtModel;
    }
}
