package nl.tudelft.sem.store.controller;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.store.NotifyStoreRequest;
import nl.tudelft.sem.store.Store;
import nl.tudelft.sem.store.StoreNotFoundException;
import nl.tudelft.sem.store.StoreRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/store") // all the mappings in this controller will be prefixed with /store
public class StoreRestController {
    StoreRepository storeRepository;

    @GetMapping("/hello")
        // is actually /store/hello
    String hello() {
        return "Hello";
    }


    @PostMapping("/notify")
        // /store/notify
    String notifyStore(@RequestBody NotifyStoreRequest notifyStoreRequest) {
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

