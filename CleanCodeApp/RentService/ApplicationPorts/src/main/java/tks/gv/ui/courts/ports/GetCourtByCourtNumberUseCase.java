package tks.gv.ui.courts.ports;

import tks.gv.Court;

public interface GetCourtByCourtNumberUseCase {
    Court getCourtByCourtNumber(int number);
}
