package nl.tudelft.sem.template.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.checkout.domain.Order;
import nl.tudelft.sem.checkout.domain.OrderNotFoundException;
import nl.tudelft.sem.checkout.domain.OrderRepository;
import nl.tudelft.sem.checkout.domain.OrderService;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderService orderService;
    private Order order;

    @BeforeEach
    public void setup() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);

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
        when(orderRepository.findAll()).thenReturn(List.of(order, o1));

        Assertions.assertThat(orderService.getAllOrders()).containsExactly(order, o1);
    }

    @Test
    public void getAllOrdersTest2() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        Assertions.assertThat(orderService.getAllOrders()).isEmpty();
    }

    @Test
    public void getOrdersForCustomerTest1() {
        Order o1 = new Order();
        String netId = "Matt";
        when(orderRepository.findOrdersForCustomer(netId)).thenReturn(List.of(order, o1));

        Assertions.assertThat(orderService.getOrdersForCustomer(netId)).containsExactly(order, o1);
    }

    @Test
    public void getOrdersForCustomerTest2() {
        String netId = "Matt";
        when(orderRepository.findOrdersForCustomer(netId)).thenReturn(new ArrayList<>());

        Assertions.assertThat(orderService.getOrdersForCustomer(netId)).isEmpty();
    }

    @Test
    public void getOrderByIdTest1() throws Exception {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void getOrderByIdTest2() throws Exception {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            orderService.getOrderById(orderId);
        }).isInstanceOf(OrderNotFoundException.class).hasMessage("1");
    }

    @Test
    public void addOrderTest() {
        orderService.addOrder(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void removeOrderTest1() throws Exception {
        long orderId = 1;
        when(orderRepository.existsById(orderId)).thenReturn(true);

        orderService.removeOrderById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    public void removeOrderTest2() throws Exception {
        long orderId = 1;
        when(orderRepository.existsById(orderId)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> {
            orderService.removeOrderById(orderId);
        }).isInstanceOf(OrderNotFoundException.class).hasMessage("1");

        verify(orderRepository, times(0)).deleteById(orderId);
    }
}
