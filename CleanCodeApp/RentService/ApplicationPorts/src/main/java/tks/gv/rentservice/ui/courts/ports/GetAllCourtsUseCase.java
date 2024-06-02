package tks.gv.rentservice.ui.courts.ports;

import tks.gv.rentservice.Court;

import java.util.List;

public interface GetAllCourtsUseCase {
    List<Court> getAllCourts();
}
