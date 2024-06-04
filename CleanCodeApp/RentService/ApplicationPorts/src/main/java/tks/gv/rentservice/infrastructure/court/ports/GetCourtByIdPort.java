package tks.gv.rentservice.infrastructure.court.ports;

import tks.gv.rentservice.Court;

import java.util.UUID;

public interface GetCourtByIdPort {
    Court getCourtById(UUID id);
}
