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
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.Getter;
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
    @Getter
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    @NotNull(message = "Pizza name can't be null")
    @Size(min = 5, max = 30, message = "Pizza name should be between 5 and 30 characters long")
    private String pizzaName;

    @ElementCollection
    @Column(name = "toppings", nullable = false)
    @Convert(converter = ToppingAttributeConverter.class)
    private List<Topping> toppings;

    @Column(name = "price", nullable = false)
    @Min(value = 5, message = "The pizza's price should be at least 5 euros")
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

    /**
     * Adds a topping to the pizza if it's not on it yet
     * @param t The topping to add
     * @return Whether the topping was added
     */
    public boolean addTopping(Topping t) {
        if(toppings.contains(t)) {
            return false;
        }
        toppings.add(t);
        price += t.getPrice();
        return true;
    }

    /**
     * Removes a topping from the pizza if it's on there
     * @param t The topping to remove
     * @return Whether the topping was removed
     */
    public boolean removeTopping(Topping t) {
        if(!toppings.contains(t)) {
            return false;
        }
        toppings.remove(t);
        price = price - t.getPrice();
        return true;
    }
}
