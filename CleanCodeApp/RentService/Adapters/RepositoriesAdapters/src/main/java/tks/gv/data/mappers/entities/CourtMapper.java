package tks.gv.data.mappers.entities;

import tks.gv.courts.Court;
import tks.gv.data.entities.CourtEntity;

import java.util.Objects;
import java.util.UUID;

public class CourtMapper {
    public static CourtEntity toMongoCourt(Court court) {
        return new CourtEntity(Objects.requireNonNullElse(court.getId(), "").toString(),
                court.getArea(),
                court.getBaseCost(),
                court.getCourtNumber(),
                court.isArchive(),
                court.isRented() ? 1 : 0);
    }

    public static Court fromMongoCourt(CourtEntity courtMapper) {
        if (courtMapper == null) return null;
        Court courtModel = new Court(UUID.fromString(courtMapper.getId()),
                courtMapper.getArea(),
                courtMapper.getBaseCost(),
                courtMapper.getCourtNumber());
        courtModel.setArchive(courtMapper.isArchive());
        courtModel.setRented(courtMapper.isRented() > 0);
        return courtModel;
    }
}
