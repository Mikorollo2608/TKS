package tks.gv.infrastructure.courts.ports;

import tks.gv.courts.Court;

public interface GetCourtByCourtNumberPort {
    Court getCourtByCourtNumber(int number);
}
