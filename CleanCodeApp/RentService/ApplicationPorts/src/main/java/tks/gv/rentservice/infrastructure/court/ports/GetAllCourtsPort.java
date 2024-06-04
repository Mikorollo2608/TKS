package tks.gv.rentservice.infrastructure.court.ports;

import tks.gv.rentservice.Court;

import java.util.List;

public interface GetAllCourtsPort {
    List<Court> getAllCourts();
}
