package tks.gv.rentservice;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tks.gv.rentservice.infrastructure.court.ports.ActivateCourtPort;
import tks.gv.rentservice.infrastructure.court.ports.AddCourtPort;
import tks.gv.rentservice.infrastructure.court.ports.DeactivateCourt;
import tks.gv.rentservice.infrastructure.court.ports.DeleteCourtPort;
import tks.gv.rentservice.infrastructure.court.ports.GetAllCourtsPort;
import tks.gv.rentservice.infrastructure.court.ports.GetCourtByCourtNumberPort;
import tks.gv.rentservice.infrastructure.court.ports.GetCourtByIdPort;
import tks.gv.rentservice.infrastructure.court.ports.ModifyCourtPort;
import tks.gv.rentservice.ui.courts.ports.ActivateCourtUseCase;
import tks.gv.rentservice.ui.courts.ports.AddCourtUseCase;
import tks.gv.rentservice.ui.courts.ports.DeactivateUseCase;
import tks.gv.rentservice.ui.courts.ports.DeleteCourtUseCase;
import tks.gv.rentservice.ui.courts.ports.GetAllCourtsUseCase;
import tks.gv.rentservice.ui.courts.ports.GetCourtByCourtNumberUseCase;
import tks.gv.rentservice.ui.courts.ports.GetCourtByIdUseCase;
import tks.gv.rentservice.ui.courts.ports.ModifyCourtUseCase;

import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class CourtService implements DeleteCourtUseCase, ActivateCourtUseCase,
        DeactivateUseCase, ModifyCourtUseCase,
        GetCourtByCourtNumberUseCase, GetCourtByIdUseCase,
        GetAllCourtsUseCase, AddCourtUseCase {

    private AddCourtPort addCourtPort;
    private GetAllCourtsPort getAllCourtsPort;
    private GetCourtByIdPort getCourtByIdPort;
    private GetCourtByCourtNumberPort getCourtByCourtNumberPort;
    private ModifyCourtPort modifyCourtPort;
    private ActivateCourtPort activateCourtPort;
    private DeactivateCourt deactivateCourtPort;
    private DeleteCourtPort deleteCourtPort;

    @Autowired
    public CourtService(AddCourtPort addCourtPort, GetAllCourtsPort getAllCourtsPort,
                        GetCourtByIdPort getCourtByIdPort, GetCourtByCourtNumberPort getCourtByCourtNumberPort,
                        ModifyCourtPort modifyCourtPort, ActivateCourtPort activateCourtPort,
                        DeactivateCourt deactivateCourtPort, DeleteCourtPort deleteCourtPort) {
        this.addCourtPort = addCourtPort;
        this.getAllCourtsPort = getAllCourtsPort;
        this.getCourtByIdPort = getCourtByIdPort;
        this.getCourtByCourtNumberPort = getCourtByCourtNumberPort;
        this.modifyCourtPort = modifyCourtPort;
        this.activateCourtPort = activateCourtPort;
        this.deactivateCourtPort = deactivateCourtPort;
        this.deleteCourtPort = deleteCourtPort;
    }

    @Override
    public Court addCourt(Court court) {
        return addCourtPort.addCourt(court);
    }

    @Override
    public List<Court> getAllCourts() {
        return getAllCourtsPort.getAllCourts();
    }

    @Override
    public Court getCourtById(UUID id) {
        return getCourtByIdPort.getCourtById(id);
    }

    @Override
    public Court getCourtByCourtNumber(int number) {
        return getCourtByCourtNumberPort.getCourtByCourtNumber(number);
    }

    @Override
    public void modifyCourt(Court court) {
        modifyCourtPort.modifyCourt(court);
    }

    @Override
    public void activateCourt(UUID id) {
        activateCourtPort.activateCourt(id);
    }

    @Override
    public void deactivateCourt(UUID id) {
        deactivateCourtPort.deactivateCourt(id);
    }

    @Override
    public void deleteCourt(UUID id) {
        deleteCourtPort.delteCourt(id);
    }

}
