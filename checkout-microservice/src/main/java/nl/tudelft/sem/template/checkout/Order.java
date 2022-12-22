package nl.tudelft.sem.template.checkout;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.PizzaAttributeConverter;
import nl.tudelft.sem.template.commons.entity.Pizza;

/**
 * Entity that stores information about a customer's order.
 */
@Entity
@Table(name = "orders")
@NoArgsConstructor
@EqualsAndHashCode
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private int orderId;

    @Column(name = "storeId", nullable = false)
    private int storeId;

    @ElementCollection
    @Column(name = "pizzas", nullable = false)
    @Convert(converter = PizzaAttributeConverter.class)
    private List<Pizza> pizzaList;

    public Order(int storeId, List<Pizza> pizzaList) {
        this.storeId = storeId;
        this.pizzaList = pizzaList;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getStoreId() {
        return storeId;
    }

    public List<Pizza> getPizzaList() {
        return pizzaList;
    }

    /**
     * Calculates the total price of the order by summing the total prices of the pizza's.
     *
     * @return The total price of the order
     */
    public int calculatePrice() {
        int price = 0;
        for (Pizza pizza : pizzaList) {
            price += pizza.calculatePrice();
        }
        return price;
    }
}
