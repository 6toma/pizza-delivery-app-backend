package nl.tudelft.sem.template.example;

import nl.tudelft.sem.checkout.domain.Order;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTest {

    Order order = new Order(1L, "Matt", LocalTime.NOON, new ArrayList<>(), List.of("ABCD12"));
    Pizza pizza1 = new Pizza("Margherita", new ArrayList<>(), 11);
    Pizza pizza2 = new Pizza("Hawaii", new ArrayList<>(), 10.5);

    @Test
    public void constructorNotNull() {
        Assertions.assertThat(order).isNotNull();
    }

    @Test
    public void getOrderIdTest() {
        Assertions.assertThat(order.getOrderId()).isEqualTo(0);

        Order order2 = new Order(1L, null, null, null, null);
        Assertions.assertThat(order2.getOrderId()).isEqualTo(0); //id changes only when the order is saved in the repo
    }

    @Test
    public void getStoreIdTest() {
        Assertions.assertThat(order.getStoreId()).isEqualTo(1);

        Order order2 = new Order(15L, null, null, null, null);
        Assertions.assertThat(order2.getStoreId()).isEqualTo(15);
    }

    @Test
    public void getPizzaListTest() {
        Assertions.assertThat(order.getPizzaList()).isEmpty();

        Order order2 = new Order(15L, null, null, null, null);
        Assertions.assertThat(order2.getPizzaList()).isNull();

        Order order3 = new Order(15L, "Matt", LocalTime.NOON, List.of(pizza1, pizza2), List.of("ABCD12"));
        Assertions.assertThat(order3.getPizzaList()).containsExactly(pizza1, pizza2);
    }

    @Test
    public void getCustomerIdTest() {
        Assertions.assertThat(order.getCustomerId()).isEqualTo("Matt");
    }

    @Test
    public void getPickupTimeTest() {
        Assertions.assertThat(order.getPickupTime()).isEqualTo(LocalTime.NOON);
    }

    @Test
    public void getCouponCodesTest() {
        Assertions.assertThat(order.getCouponCodes()).containsExactly("ABCD12");

        Order order2 = new Order(15L, null, null, null, null);
        Assertions.assertThat(order2.getPizzaList()).isNull();
    }

    @Test
    public void calculatePriceWithoutDiscountTest() {
        Assertions.assertThat(order.calculatePriceWithoutDiscount()).isEqualTo(0);

        Order order2 = new Order(15L, "Matt", LocalTime.NOON, List.of(pizza1, pizza2), List.of("ABCD12"));
        Assertions.assertThat(order2.calculatePriceWithoutDiscount()).isEqualTo(21.5);
    }
}
