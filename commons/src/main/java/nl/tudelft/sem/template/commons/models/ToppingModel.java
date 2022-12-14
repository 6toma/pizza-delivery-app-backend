package nl.tudelft.sem.template.commons.models;

import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Topping;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * Model representing an pizza.
 */
@Data
public class ToppingModel {
    private String name;
    @Min(0)
    private double price;
}