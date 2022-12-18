package nl.tudelft.sem.template.commons.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.ToppingAttributeConverter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 *
 */
@MappedSuperclass
@NoArgsConstructor
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

    public Pizza(double price, List<Topping> toppings){
        this.price = price;
        this.toppings = toppings;
    }

    /**
     *
     * @return
     */
    public int calculatePrice() {
        int price = 0;
        for(Topping topping : toppings)
            price += topping.getPrice();
        return price;
    }

    /**
     *
     * @param t
     * @return
     */
    public boolean addTopping(Topping t) {
        if(toppings.contains(t)) {
            return false;
        }
        toppings.add(t);
        return true;
    }

    /**
     *
     * @param t
     * @return
     */
    public boolean removeTopping(Topping t) {
        if(!toppings.contains(t)) {
            return false;
        }
        toppings.remove(t);
        return true;
    }
}
