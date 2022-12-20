package nl.tudelft.sem.store.controller;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreOwnerValidModel;
import nl.tudelft.sem.store.domain.StoreRepository;
import nl.tudelft.sem.template.authentication.UserEmail;
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


    /**
     * <b>GET</b> request that returns all store names in the default store db.
     *
     * @return a list of available store names
     */
    @GetMapping("/getStoreNames")
    public List<String> getStoreNames() {
        return storeRepository.getAllStoreNames();
    }

    /**
     * <b>POST</b> request that checks if the store name given is in the database.
     *
     * @param storeName the name of the store you want to check
     * @return true/false if the storeName is a valid store name
     */
    @PostMapping("/getStoreByName")
    public boolean storeNameExists(@RequestBody String storeName) {
        return storeRepository.existsByStoreName(storeName);
    }

    /**
     * <b>POST</b> request that checks if a combination between <code>storeId</code> and <code>storeOwnerEmail</code> is
     * valid. It should be used in coupon microservice to check if a store owner is legitimate.
     * <p>In case the storeId does not exist <code>400</code> response is sent.</p>
     *
     * @param storeOwnerValidModel request model that has <code>storeId</code> and <code>storeOwnerEmail</code>
     * @return true/false if the store owner is legitimate or not
     */
    @PostMapping("/checkStoreowner")
    public boolean checkStoreOwnerMatchesStoreId(@RequestBody StoreOwnerValidModel storeOwnerValidModel) {
        long storeId = storeOwnerValidModel.getStoreId();
        if (storeRepository.findById(storeId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Store with id " + storeId + " not found");
        }
        return storeRepository.existsByStoreIdAndStoreOwnerEmail(storeOwnerValidModel.getStoreId(),
            new UserEmail(storeOwnerValidModel.getEmail()));
    }

    /**
     * <b>POST</b> request to get the <code>storeId</code> given a <code>storeName</code>.
     *
     * @param storeName name of the store
     * @return the <code>storeId</code> if <code>storeName</code>is valid or <code>-1</code> if it is not.
     */
    @PostMapping("/getStoreIdFromName")
    public long storeIdFromName(@RequestBody String storeName) {
        try {
            return storeRepository.getStoreIdFromStoreName(storeName);
        } catch (Exception e) {
            // in the case when the storeName is bad we return -1
            return -1;
        }
    }

    /**
     * <b>POST</b>request to notify store from checkout.
     *
     * @param storeId storeId to notify about an order
     * @return a String that says the notification was successfully or a bad request in case of invalid storeId
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

