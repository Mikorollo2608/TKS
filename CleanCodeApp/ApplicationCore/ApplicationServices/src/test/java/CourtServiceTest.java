import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.courts.Court;

import tks.gv.courtservice.CourtService;
import tks.gv.infrastructure.courts.ports.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class CourtServiceTest {
    Court court1;
    Court court2;
    Court court3;
    List<Court> courtList;
    @Mock
    AddCourtPort addCourtPort;
    @Mock
    GetAllCourtsPort getAllCourtsPort;
    @Mock
    GetCourtByIdPort getCourtByIdPort;
    @Mock
    GetCourtByCourtNumberPort getCourtByCourtNumberPort;
    @Mock
    ModifyCourtPort modifyCourtPort;
    @Mock
    ActivateCourtPort activateCourtPort;
    @Mock
    DeactivateCourt deactivateCourtPort;
    @Mock
    DeleteCourtPort deleteCourtPort;
    @InjectMocks
    CourtService courtService = new CourtService();

    @BeforeEach
    void prepareCourts(){
        court1 = new Court(UUID.fromString("bd67f4f3-bddf-4ad8-b563-38e2c0b8d34e"), 10, 2, 1);
        court2 = new Court(UUID.fromString("8ec379f1-3c16-40d6-abab-c43b47ca4f94"), 20, 4, 2);
        court3 = new Court(UUID.fromString("b52757b9-6605-4d09-83e7-cf714dc5fb5f"), 30, 6, 3);
        courtList = new ArrayList<>();
        courtList.add(court1);
        courtList.add(court2);
        courtList.add(court3);
    }

    @Test
    void testConstructor(){
        CourtService cs = new CourtService(addCourtPort,getAllCourtsPort, getCourtByIdPort,
                getCourtByCourtNumberPort, modifyCourtPort, activateCourtPort,
                deactivateCourtPort, deleteCourtPort);
        assertNotNull(cs);
    }

    @Test
    void testGetAllCourts(){
        Mockito.when(getAllCourtsPort.getAllCourts()).thenReturn(courtList);

        List<Court> retList = courtService.getAllCourts();
        assertEquals(3, retList.size());
        assertEquals(court1, retList.get(0));
        assertEquals(court2, retList.get(1));
        assertEquals(court3, retList.get(2));
    }

    @Test
    void testAddCourtNewCourt() {
        Mockito.doNothing().when(addCourtPort).addCourt(any(Court.class));
        courtService.addCourt(court1);
        Mockito.verify(addCourtPort,Mockito.times(1)).addCourt(court1);

    }

    @Test
    void testGetCourtByCourtId() {
        Mockito.when(getCourtByIdPort.getCourtById(eq(court1.getId()))).thenReturn(court1);
        assertEquals(court1, courtService.getCourtById(court1.getId()));
    }

    @Test
    void testActivateCourt() {
        Mockito.doNothing().when(activateCourtPort).activateCourt(any(UUID.class));
        courtService.activateCourt(court1.getId());
        Mockito.verify(activateCourtPort, Mockito.times(1)).activateCourt(court1.getId());
    }

    @Test
    void testDeactivateCourt() {
        Mockito.doNothing().when(deactivateCourtPort).deactivateCourt(any(UUID.class));
        courtService.deactivateCourt(court1.getId());
        Mockito.verify(deactivateCourtPort, Mockito.times(1)).deactivateCourt(court1.getId());
    }

    @Test
    void testDeletingCourt() {
        Mockito.doNothing().when(deleteCourtPort).delteCourt(any(UUID.class));
        courtService.deleteCourt(court1.getId());
        Mockito.verify(deleteCourtPort, Mockito.times(1)).delteCourt(court1.getId());
    }

    @Test
    public void testGetCourtByCourtNumber() {
        Mockito.when(getCourtByCourtNumberPort.getCourtByCourtNumber(eq(court1.getCourtNumber()))).thenReturn(court1);
        assertEquals(court1,courtService.getCourtByCourtNumber(court1.getCourtNumber()));
    }

    @Test
    void testModifyCourt() {
        Mockito.doNothing().when(modifyCourtPort).modifyCourt(any(Court.class));
        courtService.modifyCourt(court1);
        Mockito.verify(modifyCourtPort, Mockito.times(1)).modifyCourt(court1);
    }
}
