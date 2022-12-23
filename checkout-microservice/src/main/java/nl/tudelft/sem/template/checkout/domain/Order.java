package nl.tudelft.sem.template.checkout.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime pickupTime;

    @ElementCollection
    @Column(name = "pizzas", nullable = false)
    @Convert(converter = CartPizzaAttributeConverter.class)
    private List<CartPizza> pizzaList;

    @Column(name = "coupon")
    private String coupon;

    @Column(name = "finalPrice", nullable = false)
    @Min(0)
    @Getter
    private double finalPrice;

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    /**
     * Calculates the price of the order without any discounts.
     *
     * @return The price of the order without discounts
     */
    public double calculatePriceWithoutDiscount() {
        double price = 0;
        for (CartPizza pizza : pizzaList) {
            for (int i = 0; i < pizza.getAmount(); i++) {
                price += pizza.getPizza().getPrice();
            }
        }
        return price;
    }
}
