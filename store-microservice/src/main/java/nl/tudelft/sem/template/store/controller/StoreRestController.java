package nl.tudelft.sem.template.store.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.mail.MessagingException;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.commons.models.StoreOwnerValidModel;
import nl.tudelft.sem.template.store.domain.Store;
import nl.tudelft.sem.template.store.domain.StoreRepository;
import nl.tudelft.sem.template.store.services.EmailNotificationService;
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
    private EmailNotificationService emailNotificationService;


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
     * <b>POST</b> request that checks if a combination between <code>storeId</code> and <code>storeOwnerNetId</code> is
     * valid. It should be used in coupon microservice to check if a store owner is legitimate.
     * <p>In case the storeId does not exist <code>400</code> response is sent.</p>
     *
     * @param storeOwnerValidModel request model that has <code>storeId</code> and <code>storeOwnerNetId</code>
     * @return true/false if the store owner is legitimate or not
     */
    @PostMapping("/checkStoreowner")
    public boolean checkStoreOwnerMatchesStoreId(@RequestBody StoreOwnerValidModel storeOwnerValidModel) {
        long storeId = storeOwnerValidModel.getStoreId();
        if (storeRepository.findById(storeId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Store with id " + storeId + " not found");
        }
        return storeRepository.existsByStoreIdAndStoreOwnerNetId(storeOwnerValidModel.getStoreId(),
            new NetId(storeOwnerValidModel.getNetId()));
    }

    /**
     * <b>POST</b> request to get the <code>storeId</code> given a <code>storeName</code>.
     *
     * @param storeName name of the store
     * @return the <code>storeId</code> if <code>storeName</code>is valid or <code>-1</code> if it is not.
     */
    @PostMapping("/getStoreIdFromName")
    public Long storeIdFromName(@RequestBody String storeName) {
        try {
            return storeRepository.getStoreIdFromStoreName(storeName);
        } catch (Exception e) {
            // in the case when the storeName is bad we return -1
            return -1L;
        }
    }

    /**
     * <b>POST</b>request to notify store from checkout of an order creation.
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
        try {
            emailNotificationService.notifyOrder(store.getStoreOwnerNetId().toString());
        } catch (IOException | MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't send email notification");
        }
        return "Store " + storeId + " was notified of the order.";
    }

    /**
     * <b>POST</b>request to notify store from checkout of an order removal.
     *
     * @param storeId storeId to notify about an order
     * @return a String that says the notification was successfully or a bad request in case of invalid storeId
     */
    @PostMapping("/notifyRemoveOrder")
    public String notifyRemoveOrder(@RequestBody long storeId) {
        Optional<Store> optionalStore = storeRepository.findById(storeId); // the id passed as a request might be not good
        if (optionalStore.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Store with id " + storeId + " not found");
        }
        Store store = optionalStore.get();
        try {
            emailNotificationService.notifyOrderRemove(store.getStoreOwnerNetId().toString());
        } catch (IOException | MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't send email notification");
        }
        return "Store " + storeId + " was notified of the order.";
    }

    @PostMapping("/existsByStoreId")
    public boolean existsByStoreId(@RequestBody long storeId) {
        return storeRepository.existsById(storeId);
    }
}

