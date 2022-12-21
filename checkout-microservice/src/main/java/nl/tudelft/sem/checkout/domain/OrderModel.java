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
    private final String storeName;
    private final LocalDateTime pickupTime;
    private final List<String> coupons;
}
