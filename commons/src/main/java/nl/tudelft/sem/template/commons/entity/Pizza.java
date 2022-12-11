package nl.tudelft.sem.template.commons.entity;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.ToppingAttributeConverter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
        for(Topping t : toppings) {
            builder.append(t.getName()).append(' ').append(t.getPrice()).append(";");
        }
        return builder.append(price).toString();
    }

}
