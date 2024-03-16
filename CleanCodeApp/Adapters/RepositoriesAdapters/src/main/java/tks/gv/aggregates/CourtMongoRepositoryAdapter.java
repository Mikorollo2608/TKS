package tks.gv.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.courts.Court;
import tks.gv.infrastructure.courts.ports.AddCourtPort;
import tks.gv.repositories.CourtMongoRepository;

@Component
public class CourtMongoRepositoryAdapter implements AddCourtPort {
    private final CourtMongoRepository repository;

    @Autowired
    public CourtMongoRepositoryAdapter(CourtMongoRepository repository){
        this.repository = repository;
    }
    @Override
    public void addCourt(Court court) {

    }
}
