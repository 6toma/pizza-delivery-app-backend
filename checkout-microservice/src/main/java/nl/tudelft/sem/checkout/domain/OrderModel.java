package nl.tudelft.sem.checkout.domain;

import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Pizza;

import java.time.LocalTime;
import java.util.List;

/**
 * Model representing an order
 */
@Data
public class OrderModel {
    private int storeId;
    private String customerId;
    private LocalTime pickupTime;
    private List<Pizza> pizzaList;
    private List<String> couponCodes;
}
