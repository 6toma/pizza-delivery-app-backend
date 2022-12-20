package nl.tudelft.sem.store.domain;

/*
Exception when we could not find a store in the store database
 */
public class StoreNotFoundException extends RuntimeException {

    static final long serialVersionUID = -2387516998124229948L;

    public StoreNotFoundException(Long storeId) {
        super("Could not find store with id " + storeId.toString());
    }
}