package nl.tudelft.sem.template.commons.models;

import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Topping;

import java.util.List;

/**
 * Model representing an pizza.
 */
@Data
public class ToppingModel {
    private String name;
    private double price;
}