package nl.tudelft.sem.store;

import lombok.Data;

/**
 * Simple data class that will be passed by <b>Checkout</b> microservice when an order is ready.
 * For now, it just has the <i>storeId</i>, but we will probably add an order too.
 */
@Data
public class NotifyStoreRequest {
    private long storeId;
}
