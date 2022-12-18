package nl.tudelft.sem.checkout.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.PizzaAttributeConverter;
import nl.tudelft.sem.template.commons.entity.Pizza;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private long orderId;

    @Column(name = "storeId", nullable = false)
    private long storeId;

    @Column(name = "customerId", nullable = false)
    private String customerId;

    @Column(name = "pickupTime", nullable = false)
    @Convert(converter = LocalTimeConverter.class)
    private LocalTime pickupTime;

    @ElementCollection
    @Column(name = "pizzas", nullable = false)
    @Convert(converter = PizzaAttributeConverter.class)
    private List<Pizza> pizzaList;

    @Column(name = "coupon", nullable = false)
    private String coupon;

    @Builder
    public Order(long storeId, String customerId, LocalTime pickupTime, List<Pizza> pizzaList, String coupon) {
        this.storeId = storeId;
        this.customerId = customerId;
        this.pickupTime = pickupTime;
        this.pizzaList = pizzaList;
        this.coupon = coupon;
    }

    public double calculatePriceWithoutDiscount() {
        double price = 0;
        for(Pizza pizza : pizzaList)
            price += pizza.getPrice();
        return price;
    }
}
