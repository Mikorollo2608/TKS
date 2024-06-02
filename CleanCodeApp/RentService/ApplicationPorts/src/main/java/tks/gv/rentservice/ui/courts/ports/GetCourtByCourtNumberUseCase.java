package tks.gv.rentservice.ui.courts.ports;

import tks.gv.rentservice.Court;

public interface GetCourtByCourtNumberUseCase {
    Court getCourtByCourtNumber(int number);
}
