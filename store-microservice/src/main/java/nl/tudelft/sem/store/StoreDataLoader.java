package nl.tudelft.sem.store;

import java.util.List;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreAddress;
import nl.tudelft.sem.store.domain.StoreRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * Class that loads the database with some default values.
 * It is called just after the application started
 */
@Component
@AllArgsConstructor
public class StoreDataLoader implements ApplicationRunner {
    private StoreRepository storeRepository;

    public static List<Store> DEFAULT_STORE_LIST = List.of(
            new Store(1, new StoreAddress("2628", "Street", 234)),
            new Store(2, new StoreAddress("2132", "Another Street", 123))
    );

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Saving default stores");
        storeRepository.saveAll(DEFAULT_STORE_LIST);
    }
}
