package nl.tudelft.sem.template.commons.models;

import java.util.List;
import javax.validation.constraints.Min;
import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Topping;

/**
 * Model representing an pizza.
 */
@Data
public class PizzaModel {
    private String pizzaName;
    private List<Topping> toppings;
    @Min(0)
    private double price;
}