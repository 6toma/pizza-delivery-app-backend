package nl.tudelft.sem.checkout.domain;

import java.time.LocalDateTime;
import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Pizza;

import java.time.LocalTime;
import java.util.List;

/**
 * Model representing an order
 */
@Data
public class OrderModel {
    private final int storeId;
    private final String customerId;
    private final LocalDateTime pickupTime;
    private final List<Pizza> pizzaList;
    private final String coupon;
}
