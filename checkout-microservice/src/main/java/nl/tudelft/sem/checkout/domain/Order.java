package nl.tudelft.sem.checkout.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.commons.PizzaAttributeConverter;
import nl.tudelft.sem.template.commons.ToppingAttributeConverter;
import nl.tudelft.sem.template.commons.entity.Pizza;

import javax.persistence.*;
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

    public int calculatePrice() {
        int price = 0;
        for(Pizza pizza : pizzaList)
            price += pizza.calculatePrice();
        return price;
    }
}
