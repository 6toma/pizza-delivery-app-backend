package nl.tudelft.sem.store;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * Class that loads the database with some default values
 * It is called just after the application started
 */
@Component
@AllArgsConstructor
public class DataLoader implements ApplicationRunner {
    StoreRepository storeRepository;

    @Override
    public void run(ApplicationArguments args) {
        StoreAddress address1 = new StoreAddress("2628", "Street", 234);
        StoreAddress address2 = new StoreAddress("2132", "Another Street", 123);
        storeRepository.save(new Store(1, address1));
        storeRepository.save(new Store(2, address2));
    }
}
