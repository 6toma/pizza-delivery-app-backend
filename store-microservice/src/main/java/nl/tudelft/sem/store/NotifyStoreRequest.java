package nl.tudelft.sem.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple data class that will be passed by <b>Checkout</b> microservice when an order is ready.
 * For now, it just has the <i>storeId</i>, but we will probably add an order too.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyStoreRequest {
    private long storeId;
    // TODO : maybe include order information here too
}
