package nl.tudelft.sem.template.checkout;

import java.util.List;
import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Pizza;

@Data
public class OrderModel {
    private int storeId;
    private List<Pizza> pizzaList;
}
