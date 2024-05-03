package tks.gv.infrastructure.courts.ports;

import tks.gv.Court;

import java.util.List;

public interface GetAllCourtsPort {
    List<Court> getAllCourts();
}
