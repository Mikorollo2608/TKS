package tks.gv.courtservice;

import org.springframework.stereotype.Service;
import tks.gv.courts.Court;
import tks.gv.infrastructure.courts.ports.*;
import tks.gv.userinterface.courts.ports.*;

import java.util.List;
import java.util.UUID;

@Service
public class CourtService implements DeleteCourtUseCase, ActivateCourtUseCase,
                                    DeactivateUseCase, ModifyCourtUseCase,
                                    GetCourtByCourtNumberUseCase, GetCourtByIdUseCase,
                                    GetAllCourtsUseCase, AddCourtUseCase  {

    AddCourtPort addCourtPort;
    GetAllCourtsPort getAllCourtsPort;
    GetCourtByIdPort getCourtByIdPort;
    GetCourtByCourtNumberPort getCourtByCourtNumberPort;
    ModifyCourtPort modifyCourtPort;
    ActivateCourtPort activateCourtPort;
    DeactivateCourt deactivateCourt;
    DeleteCourtPort deleteCourtPort;

    public CourtService(AddCourtPort addCourtPort, GetAllCourtsPort getAllCourtsPort,
                        GetCourtByIdPort getCourtByIdPort, GetCourtByCourtNumberPort getCourtByCourtNumberPort,
                        ModifyCourtPort modifyCourtPort, ActivateCourtPort activateCourtPort,
                        DeactivateCourt deactivateCourt, DeleteCourtPort deleteCourtPort) {
        this.addCourtPort = addCourtPort;
        this.getAllCourtsPort = getAllCourtsPort;
        this.getCourtByIdPort = getCourtByIdPort;
        this.getCourtByCourtNumberPort = getCourtByCourtNumberPort;
        this.modifyCourtPort = modifyCourtPort;
        this.activateCourtPort = activateCourtPort;
        this.deactivateCourt = deactivateCourt;
        this.deleteCourtPort = deleteCourtPort;
    }

    @Override
    public void addCourt(Court court) {
        addCourtPort.addCourt(court);
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
        deactivateCourt.deactivateCourt(id);
    }

    @Override
    public void deleteCourt(UUID id) {
        deleteCourtPort.delteCourt(id);
    }

}
