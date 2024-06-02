package tks.gv.rentservice.infrastructure.courts.ports;

import tks.gv.rentservice.Court;

public interface GetCourtByCourtNumberPort {
    Court getCourtByCourtNumber(int number);
}
