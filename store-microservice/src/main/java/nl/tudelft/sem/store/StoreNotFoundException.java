package nl.tudelft.sem.store;

public class StoreNotFoundException extends RuntimeException {

    static final long serialVersionUID = -2387516998124229948L;

    public StoreNotFoundException(Long storeId) {
        super(storeId.toString());
    }
}
