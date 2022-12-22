package nl.tudelft.sem.template.commons.models;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.hibernate.validator.constraints.Length;

/**
 * Model representing an pizza.
 */
@Data
public class PizzaModel {
    @NotNull
    @Length(min = 1)
    private String pizzaName;
    private List<String> toppings;
    @Min(0)
    private double price;
}