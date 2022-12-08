package nl.tudelft.sem.store.controller;

import java.util.Optional;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.store.NotifyStoreRequest;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreNotFoundException;
import nl.tudelft.sem.store.domain.StoreRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * All the mappings in this controller will be prefixed with <code>/store</code>.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/store")
public class StoreRestController {
    private StoreRepository storeRepository;

    /**
     * Post request to notify store from checkout.
     *
     * @param notifyStoreRequest notification request just has the id of the store
     * @return a String that says the notification was successfully
     * @throws StoreNotFoundException if the <code>storeId</code> is not in the database
     */
    @PostMapping("/notify")
    public String notifyStore(@RequestBody NotifyStoreRequest notifyStoreRequest) throws StoreNotFoundException {
        Long storeId = notifyStoreRequest.getStoreId();
        Optional<Store> optionalStore = storeRepository.findById(storeId); // the id passed as a request might be not good
        if (optionalStore.isEmpty()) {
            throw new StoreNotFoundException(storeId);
        }
        Store store = optionalStore.get();
        store.preparePizza();
        return "Store " + notifyStoreRequest.getStoreId() + " was notified. It is now preparing pizza!";
    }
}

