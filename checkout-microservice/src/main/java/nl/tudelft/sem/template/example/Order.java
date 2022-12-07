package nl.tudelft.sem.template.example;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.commons.ToppingAttributeConverter;
import nl.tudelft.sem.template.commons.entity.Pizza;

import javax.persistence.*;
import java.util.List;

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
    //@Convert(converter = ToppingAttributeConverter.class)
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

    public int calculatePrice() {
        int price = 0;
        for(Pizza pizza : pizzaList)
            price += pizza.calculatePrice();
        return price;
    }
}
