package nl.tudelft.sem.store.controller;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreNotFoundException;
import nl.tudelft.sem.store.domain.StoreOwnerValidModel;
import nl.tudelft.sem.store.domain.StoreRepository;
import nl.tudelft.sem.template.authentication.NetId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * All the mappings in this controller will be prefixed with <code>/store</code>.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/store")
public class StoreRestController {
    private StoreRepository storeRepository;


    @GetMapping("/getStoreNames")
    public List<String> getStoreNames() {
        return storeRepository.getAllStoreNames();
    }

    @GetMapping("/getStoreByName")
    public boolean storeNameExists(@RequestBody String storeName) {
        return storeRepository.existsByStoreName(storeName);
    }

    @PostMapping("/checkStoreowner")
    public boolean checkStoreOwnerMatchesStoreId(@RequestBody StoreOwnerValidModel storeOwnerValidModel) {
        long storeId = storeOwnerValidModel.getStoreId();
        if (storeRepository.findById(storeId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Store with id " + storeId + " not found");
        }
        return storeRepository.existsByStoreIdAndStoreOwnerNetId(storeOwnerValidModel.getStoreId(),
            new NetId(storeOwnerValidModel.getNetId()));
    }

    @GetMapping("/getStoreIdFromName")
    public long storeIdFromName(@RequestBody String storeName) {
        try {
            return storeRepository.getStoreIdFromStoreName(storeName);
        } catch (Exception e) {
            // in the case when the storeName is bad we return -1
            return -1;
        }
    }

    /**
     * Post request to notify store from checkout.
     *
     * @param storeId storeId to notify about an order
     * @return a String that says the notification was successfully
     * @throws StoreNotFoundException if the <code>storeId</code> is not in the database
     */
    @PostMapping("/notify")
    public String notifyStore(@RequestBody long storeId) {
        Optional<Store> optionalStore = storeRepository.findById(storeId); // the id passed as a request might be not good
        if (optionalStore.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Store with id " + storeId + " not found");
        }
        Store store = optionalStore.get();
        store.preparePizza();
        return "Store " + storeId + " was notified of the order.";
    }
}

