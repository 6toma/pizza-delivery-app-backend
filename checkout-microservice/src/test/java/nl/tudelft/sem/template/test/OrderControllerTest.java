package nl.tudelft.sem.template.test;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.checkout.controllers.OrderController;
import nl.tudelft.sem.checkout.domain.Order;
import nl.tudelft.sem.checkout.domain.OrderNotFoundException;
import nl.tudelft.sem.checkout.domain.OrderService;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderService orderService;
    private AuthManager authManager;
    private RequestHelper requestHelper;
    private Order order;
    @BeforeEach
    public void setup() {
        orderService = Mockito.mock(OrderService.class);
        authManager = Mockito.mock(AuthManager.class);
        requestHelper = Mockito.mock(RequestHelper.class);
        orderController = new OrderController(authManager, requestHelper, orderService);

        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2022, 5, 12), LocalTime.NOON);

        CustomPizza pizza1 = new CustomPizza("Margherita", 11, new ArrayList<>());
        CustomPizza pizza2 = new CustomPizza("Hawaii", 10.5, new ArrayList<>());

        order = Order.builder()
            .withStoreId(1L)
            .withCustomerId("Matt")
            .withPickupTime(ldt)
            .withPizzaList(List.of(pizza1, pizza2))
            .withCoupon("ABCD12")
            .build();
    }

    @Test
    public void getAllOrdersTest1() {
        Order o1 = new Order();
        List<Order> orderList = List.of(o1, order);
        when(orderService.getAllOrders()).thenReturn(orderList);

        Assertions.assertThat(orderController.getAllOrders()).containsExactly(o1, order);
    }

    @Test
    public void getAllOrdersTest2() {
        when(orderService.getAllOrders()).thenReturn(new ArrayList<>());

        Assertions.assertThat(orderController.getAllOrders()).isEmpty();
    }

    @Test
    public void getOrderByIdTest1() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void getOrderByIdTest2() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderById(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"1\"");
    }

    @Test
    public void getOrderByIdTest3() throws Exception {
        when(authManager.getRole()).thenReturn("ROLE_REGIONAL_MANAGER");

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void getOrderByIdTest4() throws Exception {
        when(authManager.getRole()).thenReturn("ROLE_STORE_OWNER");

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void getOrderByIdTest5() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(new ArrayList<>());

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderById(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"Order does not belong to customer, so they cannot check it\"");
    }

    @Test
    public void getPriceForEachPizzaTest1() throws Exception {
        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getPriceForEachPizza(orderId)).contains(Double.valueOf(11), 10.5);
    }

    @Test
    public void getPriceForEachPizzaTest2() throws Exception {
        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        Assertions.assertThatThrownBy(() -> {
            orderController.getPriceForEachPizza(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"1\"");
    }

    @Test
    public void getOrderPriceTest1() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderPrice(orderId)).isEqualTo(21.5);
    }

    @Test
    public void getOrderPriceTest2() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderPrice(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"1\"");
    }

    @Test
    public void getOrderPriceTest3() throws Exception {
        when(authManager.getRole()).thenReturn("ROLE_REGIONAL_MANAGER");

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderPrice(orderId)).isEqualTo(21.5);
    }

    @Test
    public void getOrderPriceTest4() throws Exception {
        when(authManager.getRole()).thenReturn("ROLE_STORE_OWNER");

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderPrice(orderId)).isEqualTo(21.5);
    }

    @Test
    public void getOrderPriceTest5() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(new ArrayList<>());

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderPrice(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"Order does not belong to customer, so they cannot check the price\"");
    }
}
