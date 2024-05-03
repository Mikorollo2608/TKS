package tks.gv.infrastructure.courts.ports;

import tks.gv.Court;

public interface GetCourtByCourtNumberPort {
    Court getCourtByCourtNumber(int number);
}
