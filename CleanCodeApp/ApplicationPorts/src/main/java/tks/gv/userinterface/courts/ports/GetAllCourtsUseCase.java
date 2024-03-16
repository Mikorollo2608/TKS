package tks.gv.userinterface.courts.ports;

import tks.gv.courts.Court;

import java.util.List;

public interface GetAllCourtsUseCase {
    List<Court> getAllCourts();
}
