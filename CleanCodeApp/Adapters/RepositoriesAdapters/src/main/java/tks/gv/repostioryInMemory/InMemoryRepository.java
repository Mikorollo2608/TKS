package tks.gv.repostioryInMemory;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryRepository<T> {

    List<T> list = new ArrayList<>();

    public void create(T initUser) {
        list.add(initUser);
    }

    public List<T> readAll() {
        return list;
    }
}
