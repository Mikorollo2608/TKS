package tks.gv.userinterface.courts.ports;

import tks.gv.courts.Court;

import java.util.List;
import java.util.UUID;

public interface CourtsUseCase {
    void addCourt(Court court);

    List<Court> getAllCourts();

    Court getCourtById(UUID id);

    Court getCourtByCourtNumber(int number);

    void modifyCourt(Court court);

    void activateCourt(UUID id);

    void deactivateCourt(UUID id);

    void deleteCourt(UUID id);
}
