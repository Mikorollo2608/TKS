package tks.gv.rentservice.infrastructure.courts.ports;

import tks.gv.rentservice.Court;

import java.util.List;

public interface GetAllCourtsPort {
    List<Court> getAllCourts();
}
