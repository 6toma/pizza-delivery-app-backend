package nl.tudelft.sem.template.commons.entity;

import java.util.HashSet;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@MappedSuperclass
@NoArgsConstructor
@ToString
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    @Getter
    private int id;

    @Column(name = "toppings", nullable = false)
    @ManyToMany
    @Getter
    private List<Topping> toppings;

    @Column(name = "price", nullable = false)
    @Min(value = 5, message = "The pizza's price should be at least 5 euros")
    @Getter
    private double price;

    public Pizza(double price, List<Topping> toppings) {
        this.price = price;
        this.toppings = toppings;
    }

    /**
     * Calculates the price as the sum of the defaultPrice and toppings price.
     *
     * @return total price of the pizza
     */
    public double calculatePrice() {
        double sum = price;
        for (Topping topping : toppings) {
            sum += topping.getPrice();
        }
        return sum;
    }

    /**
     * @param t
     * @return
     */
    public boolean addTopping(Topping t) {
        if (toppings.contains(t)) {
            return false;
        }
        toppings.add(t);
        return true;
    }

    /**
     * @param t
     * @return
     */
    public boolean removeTopping(Topping t) {
        if (!toppings.contains(t)) {
            return false;
        }
        toppings.remove(t);
        return true;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Pizza) {
            Pizza that = (Pizza) o;
            return that.getPrice() == this.getPrice() &&
                new HashSet<>(toppings).equals(new HashSet<>(that.getToppings()));
        }
        return false;
    }
}
