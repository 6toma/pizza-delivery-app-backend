package nl.tudelft.sem.template.commons.models;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PizzaToppingModel {
    @NotNull
    private int pizzaId;
    @NotNull
    private int toppingId;
}
