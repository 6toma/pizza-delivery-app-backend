package nl.tudelft.sem.template.checkout.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.template.commons.CartPizzaAttributeConverter;
import nl.tudelft.sem.template.commons.models.CartPizza;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
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
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime pickupTime;

    @ElementCollection
    @Column(name = "pizzas", nullable = false)
    @Convert(converter = CartPizzaAttributeConverter.class)
    private List<CartPizza> pizzaList;

    @Column(name = "coupon")
    private String coupon;

    public static OrderBuilder builder(){
        return new OrderBuilder();
    }

    public double calculatePriceWithoutDiscount() {
        double price = 0;
        for(CartPizza pizza : pizzaList)
            for(int i = 0; i < pizza.getAmount(); i++)
                price += pizza.getPizza().getPrice();
        return price;
    }
}
