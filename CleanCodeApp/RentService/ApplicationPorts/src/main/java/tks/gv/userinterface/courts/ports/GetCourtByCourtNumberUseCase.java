package tks.gv.userinterface.courts.ports;

import tks.gv.Court;

public interface GetCourtByCourtNumberUseCase {
    Court getCourtByCourtNumber(int number);
}
