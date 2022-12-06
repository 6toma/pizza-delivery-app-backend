package nl.tudelft.sem.template.example;

import lombok.NoArgsConstructor;

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

    public Pizza(String pizzaType, List<Topping> toppings) {
        this.pizzaName = pizzaType;
        this.toppings = toppings;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public List<Topping> getToppings() {
        return toppings;
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
}
