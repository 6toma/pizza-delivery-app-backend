package nl.tudelft.sem.template.checkout;

import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Pizza;

import java.util.List;

@Data
public class OrderModel {
    private int storeId;
    private List<Pizza> pizzaList;
}
