package nl.tudelft.sem.template.commons.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Model representing an pizza.
 */
@Data
public class ToppingModel {
    @NotNull
    @Length(min = 1)
    private String name;
    @Min(0)
    private double price;
}