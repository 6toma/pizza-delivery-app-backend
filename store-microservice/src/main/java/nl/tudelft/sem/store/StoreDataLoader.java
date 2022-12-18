package nl.tudelft.sem.store;

import java.util.List;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreAddress;
import nl.tudelft.sem.store.domain.StoreRepository;
import nl.tudelft.sem.template.authentication.NetId;
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
        Store.builder().storeId(1).storeName("Delft Dehoven").storeOwnerNetId(new NetId("Owner1"))
            .location(new StoreAddress("2624AK", "Papsouwselaan", 123)).build(),
        Store.builder().storeId(2).storeName("Delft Uni").storeOwnerNetId(new NetId("Owner2"))
            .location(new StoreAddress("2611BK", "Binnenwatersloot ", 12)).build(),
        Store.builder().storeId(3).storeName("Rotterdam").storeOwnerNetId(new NetId("Owner3"))
            .location(new StoreAddress("1641BK", "Rotterdam Street", 52)).build()
    );

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Saving default stores");
        storeRepository.saveAll(DEFAULT_STORE_LIST);
    }
}
