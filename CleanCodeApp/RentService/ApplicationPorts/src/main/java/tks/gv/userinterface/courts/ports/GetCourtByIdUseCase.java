package tks.gv.userinterface.courts.ports;

import tks.gv.Court;

import java.util.UUID;

public interface GetCourtByIdUseCase {
    Court getCourtById(UUID id);
}
