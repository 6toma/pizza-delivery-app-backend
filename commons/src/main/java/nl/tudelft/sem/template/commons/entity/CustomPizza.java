package nl.tudelft.sem.template.commons.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import lombok.ToString;


@Entity
@Table(name = "custom_pizza")
@NoArgsConstructor
@ToString(callSuper = true)
public class CustomPizza extends Pizza {

    @Column(name = "name", nullable = false)
    @NotNull(message = "Pizza name can't be null")
    @Size(min = 5, max = 30, message = "Pizza name should be between 5 and 30 characters long")
    @Getter
    private String pizzaName;

    public CustomPizza(String pizzaName, double price, List<Topping> toppingList) {
        super(price, toppingList);
        this.pizzaName = pizzaName;
    }

    public static CustomPizza CustomPizzaCreator(DefaultPizza pizza) {
        return new CustomPizza(pizza.getPizzaName(), pizza.getPrice(), new ArrayList<>(pizza.getToppings()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof CustomPizza) {
            CustomPizza that = (CustomPizza) other;
            // this.getToppings().get(0).equals(that.getToppings().get(0))
            return that.getPizzaName().equals(this.getPizzaName()) && this.getPrice() == that.getPrice() &&
                new HashSet<>(this.getToppings()).equals(new HashSet<>(that.getToppings()));
        }
        return false;
    }
    @Override
    public int hashCode(){
        return Objects.hash(this.getPizzaName(),this.getPrice(),this.getToppings());
    }

}
