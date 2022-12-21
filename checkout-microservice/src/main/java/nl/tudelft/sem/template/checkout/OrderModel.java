package nl.tudelft.sem.template.checkout;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.entity.Pizza;

@Data
@AllArgsConstructor // so that I can have a constructor to create the OrderModel
@NoArgsConstructor // required for Json to be created
public class OrderModel {
    private int storeId;
    private List<Pizza> pizzaList;
}