package nl.tudelft.sem.template.commons.models;

import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Pizza;

import javax.validation.constraints.NotNull;

@Data
public class PizzaToppingModel {
    @NotNull
    private Pizza pizza;
    @NotNull
    private String topping;
}
