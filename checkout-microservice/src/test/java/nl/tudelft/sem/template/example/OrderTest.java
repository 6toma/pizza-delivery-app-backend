package nl.tudelft.sem.template.example;

import java.util.ArrayList;
import nl.tudelft.sem.template.checkout.Order;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTest {

    @Test
    public void example1() {
        Order order = new Order(1, new ArrayList<>());
        Assertions.assertThat(order).isNotNull();
    }
}
