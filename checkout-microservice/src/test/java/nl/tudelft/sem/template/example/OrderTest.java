package nl.tudelft.sem.template.example;

import nl.tudelft.sem.template.checkout.Order;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTest {

    @Test
    public void example1() {
        Order order = new Order(1, new ArrayList<>());
        Assertions.assertThat(order).isNotNull();
    }
}
