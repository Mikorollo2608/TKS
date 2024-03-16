package tks.gv.courtservice;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tks.gv.courts.Court;
import tks.gv.infrastructure.courts.ports.AddCourtPort;
import tks.gv.userinterface.courts.ports.CourtsUseCase;

@Service
@NoArgsConstructor
public class CourtService implements CourtsUseCase {

    AddCourtPort addCourtPort;

    @Autowired
    public CourtService(AddCourtPort addCourtPort) {
        this.addCourtPort = addCourtPort;
    }

    @Override
    public void addCourt(Court court) {

    }
}
