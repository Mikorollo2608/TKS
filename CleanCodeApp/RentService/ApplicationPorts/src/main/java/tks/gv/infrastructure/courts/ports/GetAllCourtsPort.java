package tks.gv.infrastructure.courts.ports;

import tks.gv.courts.Court;

import java.util.List;

public interface GetAllCourtsPort {
    List<Court> getAllCourts();
}
