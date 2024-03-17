package tks.gv.userinterface.courts.ports;

import tks.gv.courts.Court;

public interface GetCourtByCourtNumberUseCase {
    Court getCourtByCourtNumber(int number);
}
