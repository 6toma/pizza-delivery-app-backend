package nl.tudelft.sem.template.checkout.domain;

import java.time.LocalDateTime;
import java.util.List;
import nl.tudelft.sem.template.commons.models.CartPizza;

public class OrderBuilder {
    private transient long storeId;
    private transient String customerId;
    private transient LocalDateTime pickupTime;
    private transient List<CartPizza> pizzaList;
    private transient String coupon;
    private transient double finalPrice;

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

    public OrderBuilder withFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
        return this;
    }

    /**
     * Builds the order using the information given in the builder.
     *
     * @return The actual order object
     */
    public Order build() {
        Order order = new Order();
        order.setStoreId(storeId);
        order.setCustomerId(customerId);
        order.setCoupon(coupon);
        order.setPickupTime(pickupTime);
        order.setPizzaList(pizzaList);
        order.setFinalPrice(finalPrice);
        return order;
    }
}
