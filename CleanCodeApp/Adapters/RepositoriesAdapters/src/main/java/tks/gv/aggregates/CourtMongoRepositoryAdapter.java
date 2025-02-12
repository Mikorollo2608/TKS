package tks.gv.aggregates;

import com.mongodb.client.model.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.courts.Court;
import tks.gv.data.entities.CourtEntity;
import tks.gv.data.mappers.entities.CourtMapper;
import tks.gv.exceptions.CourtException;
import tks.gv.exceptions.CourtNumberException;
import tks.gv.infrastructure.courts.ports.*;
import tks.gv.repositories.CourtMongoRepository;

import java.util.List;
import java.util.UUID;

@Component
public class CourtMongoRepositoryAdapter implements AddCourtPort, GetAllCourtsPort, GetCourtByIdPort,
        GetCourtByCourtNumberPort, ModifyCourtPort, ActivateCourtPort, DeactivateCourt, DeleteCourtPort {
    private final CourtMongoRepository repository;

    @Autowired
    public CourtMongoRepositoryAdapter(CourtMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Court addCourt(Court court) {
        return CourtMapper.fromMongoCourt(repository.create(CourtMapper.toMongoCourt(court)));
    }

    @Override
    public List<Court> getAllCourts() {
        return repository.readAll().stream().map(CourtMapper::fromMongoCourt).toList();
    }

    @Override
    public Court getCourtById(UUID id) {
        return CourtMapper.fromMongoCourt(repository.readByUUID(id));
    }

    @Override
    public Court getCourtByCourtNumber(int number) {
        List<CourtEntity> courts =  repository.read(Filters.eq("courtnumber",number));
        if (courts.isEmpty()) return null;
        return CourtMapper.fromMongoCourt(courts.get(0));
    }

    @Override
    public void modifyCourt(Court court) {
        var list = repository.read(Filters.and(
                Filters.eq("courtnumber", court.getCourtNumber()),
                Filters.ne("_id", court.getId().toString())));
        if (!list.isEmpty()) {
            throw new CourtNumberException("Nie udalo sie zmodyfikowac podanego boiska - " +
                    "proba zmiany numeru boiska na numer wystepujacy juz u innego boiska");
        }
        if (!repository.updateByReplace(court.getId(), CourtMapper.toMongoCourt(court))) {
            throw new CourtException("Nie udalo się zmodyfikowac podanego boiska");
        }
    }

    @Override
    public void activateCourt(UUID id) {
        repository.update(id, "archive", false);
    }

    @Override
    public void deactivateCourt(UUID id) {
        repository.update(id, "archive", true);
    }


    @Override
    public void delteCourt(UUID id) {
        repository.delete(id);
    }
}
