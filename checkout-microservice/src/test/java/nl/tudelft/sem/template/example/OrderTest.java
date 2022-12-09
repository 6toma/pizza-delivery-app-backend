package nl.tudelft.sem.template.example;

import nl.tudelft.sem.checkout.domain.Order;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTest {

    @Test
    public void constructorNotNull() {
        Order order = new Order(1, new ArrayList<>());
        Assertions.assertThat(order).isNotNull();
    }

    @Test
    public void getOrderIdTest() {
        Order order1 = new Order(1, new ArrayList<>());
        Assertions.assertThat(order1.getOrderId()).isEqualTo(0);

        Order order2 = new Order(1, new ArrayList<>());
        Assertions.assertThat(order2.getOrderId()).isEqualTo(0); //id changes only when the order is saved in the repo
    }

    @Test
    public void getStoreIdTest() {
        Order order1 = new Order(1, new ArrayList<>());
        Assertions.assertThat(order1.getStoreId()).isEqualTo(1);

        Order order2 = new Order(15, new ArrayList<>());
        Assertions.assertThat(order2.getStoreId()).isEqualTo(15);
    }

    @Test
    public void getPizzaListTest() {
        Order order1 = new Order(1, new ArrayList<>());
        Assertions.assertThat(order1.getPizzaList()).isEmpty();

        Order order2 = new Order(15, null);
        Assertions.assertThat(order2.getPizzaList()).isNull();
    }
}
