package nl.tudelft.sem.template.commons.models;

import javax.validation.constraints.Min;
import lombok.Data;

/**
 * Model representing an pizza.
 */
@Data
public class ToppingModel {
    private String name;
    @Min(0)
    private double price;
}