package nl.tudelft.sem.template.commons.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.entity.CustomPizza;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartPizza {

    private CustomPizza pizza;
    private int amount;

}
