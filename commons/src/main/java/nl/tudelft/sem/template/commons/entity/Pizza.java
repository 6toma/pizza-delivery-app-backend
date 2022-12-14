package nl.tudelft.sem.template.commons.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.ToppingAttributeConverter;

/**
 * Pizza entity.
 */
@Entity
@Table(name = "pizzas")
@NoArgsConstructor
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String pizzaName;

    @ElementCollection
    @Column(name = "toppings", nullable = false)
    @Convert(converter = ToppingAttributeConverter.class)
    private List<Topping> toppings;

    @Column(name = "price", nullable = false)
    private double price;

    /**
     * Creates a new pizza object.
     *
     * @param pizzaType The type of pizza
     * @param toppings  The list of toppings on the pizza
     * @param price     The price of the pizzak
     */
    public Pizza(String pizzaType, List<Topping> toppings, double price) {
        this.pizzaName = pizzaType;
        this.toppings = toppings;
        this.price = price;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pizza p = (Pizza) o;
        return id == (p.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pizzaName);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(pizzaName).append(";");
        for (Topping t : toppings) {
            builder.append(t.getName()).append(' ').append(t.getPrice()).append(";");
        }
        return builder.append(price).toString();
    }

    /**
     * Calculates the total price of the pizza by summing the prices of all its toppings.
     *
     * @return The total price of the pizza
     */
    public int calculatePrice() {
        int price = 0;
        for (Topping topping : toppings) {
            price += topping.getPrice();
        }
        return price;
    }
}
