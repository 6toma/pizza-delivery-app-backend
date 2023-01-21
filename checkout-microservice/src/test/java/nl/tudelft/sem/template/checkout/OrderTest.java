package nl.tudelft.sem.template.checkout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.checkout.domain.Order;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.models.CartPizza;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTest {

    LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2022, 5, 12), LocalTime.NOON);

    Order order = Order.builder()
        .withStoreId(1L)
        .withCustomerId("Matt")
        .withPickupTime(ldt)
        .withPizzaList(new ArrayList<>())
        .withCoupon("ABCD12")
        .build();

    Order order2 = Order.builder()
        .withStoreId(15L)
        .withCustomerId(null)
        .withPickupTime(null)
        .withPizzaList(null)
        .withCoupon(null)
        .build();

    CustomPizza cpizza1 = new CustomPizza("Margherita", 11, new ArrayList<>());
    CustomPizza cpizza2 = new CustomPizza("Hawaii", 10.5, new ArrayList<>());

    CartPizza pizza1 = new CartPizza(cpizza1, 1);
    CartPizza pizza2 = new CartPizza(cpizza2, 2);

    @Test
    public void constructorNotNull() {
        Assertions.assertThat(order).isNotNull();
    }

    @Test
    public void getOrderIdTest() {
        Assertions.assertThat(order.getOrderId()).isEqualTo(0);

        Order order2 = Order.builder().withStoreId(1L).build();
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
            .withPizzaList(List.of(pizza1, pizza2))
            .build();
        Assertions.assertThat(order3.getPizzaList()).containsExactly(pizza1, pizza2);
    }

    @Test
    public void getCustomerIdTest() {
        Assertions.assertThat(order.getCustomerId()).isEqualTo("Matt");
    }

    @Test
    public void getPickupTimeTest() {
        Assertions.assertThat(order.getPickupTime()).isEqualTo(ldt);
    }

    @Test
    public void getCouponTest() {
        Assertions.assertThat(order.getCoupon()).isEqualTo("ABCD12");

        Assertions.assertThat(order2.getPizzaList()).isNull();
    }

    @Test
    public void getFinalPriceTest() {
        Order o = Order.builder()
            .withStoreId(1L)
            .withCustomerId("Matt")
            .withPickupTime(ldt)
            .withPizzaList(new ArrayList<>())
            .withCoupon("ABCD12")
            .withFinalPrice(10.5)
            .build();

        Assertions.assertThat(o.getFinalPrice()).isEqualTo(10.5);
    }

    @Test
    public void calculatePriceWithoutDiscountTest() {
        Assertions.assertThat(order.calculatePriceWithoutDiscount()).isEqualTo(0);

        Order order3 = Order.builder()
            .withStoreId(1L)
            .withCustomerId("Matt")
            .withPickupTime(ldt)
            .withPizzaList(List.of(pizza1, pizza2))
            .withCoupon("ABCD12")
            .build();
        Assertions.assertThat(order3.calculatePriceWithoutDiscount()).isEqualTo(32);
    }
}
