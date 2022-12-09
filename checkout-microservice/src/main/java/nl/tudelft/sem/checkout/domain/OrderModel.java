package nl.tudelft.sem.checkout.domain;

import lombok.Data;
import nl.tudelft.sem.template.commons.entity.Pizza;

import java.util.List;

@Data
public class OrderModel {
    private int storeId;
    private List<Pizza> pizzaList;
}
