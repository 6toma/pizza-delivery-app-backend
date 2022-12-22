package nl.tudelft.sem.template.checkout.domain;

import java.time.LocalDateTime;
import java.util.List;
import nl.tudelft.sem.template.commons.models.CartPizza;

public class OrderBuilder {
    private long storeId;
    private String customerId;
    private LocalDateTime pickupTime;
    private List<CartPizza> pizzaList;
    private String coupon;

    public OrderBuilder withStoreId(long storeId) {
        this.storeId = storeId;
        return this;
    }

    public OrderBuilder withCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderBuilder withPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
        return this;
    }

    public OrderBuilder withPizzaList(List<CartPizza> pizzaList) {
        this.pizzaList = pizzaList;
        return this;
    }

    public OrderBuilder withCoupon(String coupon) {
        this.coupon = coupon;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setStoreId(storeId);
        order.setCustomerId(customerId);
        order.setCoupon(coupon);
        order.setPickupTime(pickupTime);
        order.setPizzaList(pizzaList);
        return order;
    }
}
