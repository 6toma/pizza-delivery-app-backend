package nl.tudelft.sem.template.commons.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Entity
@Table(name = "custom_pizza")
@NoArgsConstructor
public class CustomPizza extends Pizza {

    @Column(name = "name", nullable = false)
    @NotNull(message = "Pizza name can't be null")
    @Size(min = 5, max = 30, message = "Pizza name should be between 5 and 30 characters long")
    private String pizzaName;

    public CustomPizza(String pizzaName, double price, List<Topping> toppingList) {
        super(price, toppingList);
        this.pizzaName = pizzaName;
    }

    public static CustomPizza CustomPizzaCreator(DefaultPizza pizza) {
        return new CustomPizza(pizza.getPizzaName(), pizza.getPrice(), pizza.getToppings());
    }

}
