package nl.tudelft.sem.template.example;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.checkout.domain.Order;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTest {

    Order order = Order.builder()
        .storeId(1L)
        .customerId("Matt")
        .pickupTime(LocalTime.NOON)
        .pizzaList(new ArrayList<>())
        .coupon("ABCD12")
        .build();

    Order order2 = Order.builder()
        .storeId(15L)
        .customerId(null)
        .pickupTime(null)
        .pizzaList(null)
        .coupon(null)
        .build();

    CustomPizza pizza1 = new CustomPizza("Margherita", 11, new ArrayList<>());
    CustomPizza pizza2 = new CustomPizza("Hawaii", 10.5, new ArrayList<>());

    @Test
    public void constructorNotNull() {
        Assertions.assertThat(order).isNotNull();
    }

    @Test
    public void getOrderIdTest() {
        Assertions.assertThat(order.getOrderId()).isEqualTo(0);

        Order order2 = Order.builder()
            .storeId(1L)
            .customerId(null)
            .pickupTime(null)
            .pizzaList(null)
            .coupon(null)
            .build();
        Assertions.assertThat(order2.getOrderId()).isEqualTo(0); //id changes only when the order is saved in the repo
    }

    @Test
    public void getStoreIdTest() {
        Assertions.assertThat(order.getStoreId()).isEqualTo(1);


        Assertions.assertThat(order2.getStoreId()).isEqualTo(15);
    }

    @Test
    public void getPizzaListTest() {
        Assertions.assertThat(order.getPizzaList()).isEmpty();

        Assertions.assertThat(order2.getPizzaList()).isNull();

        Order order3 = Order.builder()
            .storeId(1L)
            .customerId("Matt")
            .pickupTime(LocalTime.NOON)
            .pizzaList(List.of(pizza1, pizza2))
            .coupon("ABCD12")
            .build();
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
    public void getCouponTest() {
        Assertions.assertThat(order.getCoupon()).isEqualTo("ABCD12");

        Assertions.assertThat(order2.getPizzaList()).isNull();
    }

    @Test
    public void calculatePriceWithoutDiscountTest() {
        Assertions.assertThat(order.calculatePriceWithoutDiscount()).isEqualTo(0);

        Order order3 = Order.builder()
            .storeId(1L)
            .customerId("Matt")
            .pickupTime(LocalTime.NOON)
            .pizzaList(List.of(pizza1, pizza2))
            .coupon("ABCD12")
            .build();
        Assertions.assertThat(order3.calculatePriceWithoutDiscount()).isEqualTo(21.5);
    }
}
