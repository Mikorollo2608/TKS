package tks.gv.infrastructure.courts.ports;

import tks.gv.Court;

import java.util.UUID;

public interface GetCourtByIdPort {
    Court getCourtById(UUID id);
}
