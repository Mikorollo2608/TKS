package adaptersTests;

import com.mongodb.client.model.Filters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tks.gv.aggregates.CourtMongoRepositoryAdapter;
import tks.gv.courts.Court;
import tks.gv.data.entities.CourtEntity;
import tks.gv.data.mappers.entities.CourtMapper;
import tks.gv.exceptions.CourtException;
import tks.gv.exceptions.CourtNumberException;
import tks.gv.repositories.CourtMongoRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class CourtMongoRepositoryAdapterTest {
    @Mock
    CourtMongoRepository repository;
    @InjectMocks
    CourtMongoRepositoryAdapter adapter;
    Court court1;
    Court court2;
    Court court3;
    List<Court> courtList;

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
        CourtMongoRepositoryAdapter courtMongoRepositoryAdapter = new CourtMongoRepositoryAdapter(repository);
        assertNotNull(courtMongoRepositoryAdapter);
    }

    @Test
    void testAddCourt(){
        Mockito.when(repository.create(eq(CourtMapper.toMongoCourt(court1)))).thenReturn(CourtMapper.toMongoCourt(court1));
        adapter.addCourt(court1);
        Mockito.verify(repository, Mockito.times(1)).create(CourtMapper.toMongoCourt(court1));
    }

    @Test
    void testGetAllCourts(){
        List<CourtEntity> courtEntityList = courtList.stream().map(CourtMapper::toMongoCourt).toList();
        Mockito.when(repository.readAll()).thenReturn(courtEntityList);
        List<Court> retList = adapter.getAllCourts();
        assertEquals(court1, retList.get(0));
        assertEquals(court2, retList.get(1));
        assertEquals(court3, retList.get(2));
    }

    @Test
    void testGetCourtByCourtNumberCourtFound(){
        Mockito.when(repository.read(eq(Filters.eq("courtnumber",1))))
                .thenReturn(Arrays.asList(CourtMapper.toMongoCourt(court1)));
        Court retCourt = adapter.getCourtByCourtNumber(court1.getCourtNumber());
        assertEquals(court1, retCourt);
    }

    @Test
    void testGetCourtByCourtNumberNotFound(){
        Mockito.when(repository.read(eq(Filters.eq("courtnumber",1))))
                .thenReturn(new ArrayList<CourtEntity>());
        Court retCourt = adapter.getCourtByCourtNumber(court1.getCourtNumber());
        assertNull(retCourt);
    }

    @Test
    void testModifyCourtSuccessful(){
        Mockito.when(repository.read(eq(Filters.and(
                Filters.eq("courtnumber", court1.getCourtNumber()),
                Filters.ne("_id", court1.getId().toString()))))).thenReturn(new ArrayList<>());
        Mockito.when(repository.updateByReplace(eq(court1.getId()), eq(CourtMapper.toMongoCourt(court1)))).thenReturn(true);

        adapter.modifyCourt(court1);

        Mockito.verify(repository, Mockito.times(1)).read(Filters.and(
                Filters.eq("courtnumber", court1.getCourtNumber()),
                Filters.ne("_id", court1.getId().toString())));
        Mockito.verify(repository, Mockito.times(1)).updateByReplace(court1.getId(), CourtMapper.toMongoCourt(court1));
    }

    @Test
    void testModifyCourtCourtNumberOccupied(){
        Mockito.when(repository.read(eq(Filters.and(
                Filters.eq("courtnumber", court1.getCourtNumber()),
                Filters.ne("_id", court1.getId().toString()))))).thenReturn(List.of(CourtMapper.toMongoCourt(court2)));

        assertThrows(CourtNumberException.class, () -> adapter.modifyCourt(court1));
    }

    @Test
    void testModifyCourtCourtFailedToUpdate(){
        Mockito.when(repository.read(eq(Filters.and(
                Filters.eq("courtnumber", court1.getCourtNumber()),
                Filters.ne("_id", court1.getId().toString()))))).thenReturn(new ArrayList<>());
        Mockito.when(repository.updateByReplace(eq(court1.getId()), eq(CourtMapper.toMongoCourt(court1)))).thenReturn(false);
        assertThrows(CourtException.class, () -> adapter.modifyCourt(court1));
    }

    @Test
    void testActivateCourt(){
        Mockito.when(repository.update(eq(court1.getId()), eq("archive"), eq(false))).thenReturn(true);
        adapter.activateCourt(court1.getId());
        Mockito.verify(repository, Mockito.times(1)).update(court1.getId(), "archive", false);
    }

    @Test
    void testDeactivateCourt(){
        Mockito.when(repository.update(eq(court1.getId()), eq("archive"), eq(true))).thenReturn(true);
        adapter.deactivateCourt(court1.getId());
        Mockito.verify(repository, Mockito.times(1)).update(court1.getId(), "archive", true);
    }

    @Test
    void testDeleteCourt(){
        Mockito.when(repository.delete(eq(court1.getId()))).thenReturn(true);
        adapter.delteCourt(court1.getId());
        Mockito.verify(repository, Mockito.times(1)).delete(court1.getId());
    }
}
