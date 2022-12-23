package nl.tudelft.sem.template.cart.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartResponse {

    private int id;
    private boolean hasAllergens;

}
