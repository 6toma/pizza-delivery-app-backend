package nl.tudelft.sem.template.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.checkout.domain.OrderModel;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderModelTest {


    CustomPizza pizza1 = new CustomPizza("Margherita", 11, new ArrayList<>());
    CustomPizza pizza2 = new CustomPizza("Hawaii", 10.5, new ArrayList<>());

    LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2022, 5, 12), LocalTime.NOON);

    OrderModel om = new OrderModel(1, "Matt", ldt, List.of(pizza1, pizza2), "ABCD12");


    @Test
    public void constructorTest() {
        Assertions.assertThat(om).isNotNull();
    }

    @Test
    public void getCustomerIdTest() {
        Assertions.assertThat(om.getCustomerId()).isEqualTo("Matt");
    }

    @Test
    public void getLocalDateTimeTest() {
        Assertions.assertThat(om.getPickupTime()).isEqualTo(ldt);
    }

    @Test
    public void getPizzaList() {
        Assertions.assertThat(om.getPizzaList()).containsExactly(pizza1, pizza2);
    }

    @Test
    public void getCouponTest() {
        Assertions.assertThat(om.getCoupon()).isEqualTo("ABCD12");
    }

    @Test
    public void getStoreIdTest() {
        Assertions.assertThat(om.getStoreId()).isEqualTo(1);
    }
}
