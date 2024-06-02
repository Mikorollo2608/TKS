package tks.gv.rentservice.infrastructure.courts.ports;

import tks.gv.rentservice.Court;

import java.util.UUID;

public interface GetCourtByIdPort {
    Court getCourtById(UUID id);
}
