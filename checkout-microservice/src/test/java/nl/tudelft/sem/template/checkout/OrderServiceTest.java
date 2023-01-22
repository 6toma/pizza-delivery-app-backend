package nl.tudelft.sem.template.checkout;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.checkout.domain.Order;
import nl.tudelft.sem.template.checkout.domain.OrderNotFoundException;
import nl.tudelft.sem.template.checkout.domain.OrderRepository;
import nl.tudelft.sem.template.checkout.domain.OrderService;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.models.CartPizza;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderService orderService;
    private Order order;

    @BeforeEach
    void setup() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);

        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2022, 5, 12), LocalTime.NOON);

        CustomPizza cpizza1 = new CustomPizza("Margherita", 11, new ArrayList<>());
        CustomPizza cpizza2 = new CustomPizza("Hawaii", 10.5, new ArrayList<>());

        CartPizza pizza1 = new CartPizza(cpizza1, 1);
        CartPizza pizza2 = new CartPizza(cpizza2, 2);

        order = Order.builder()
            .withStoreId(1L)
            .withCustomerId("Matt")
            .withPickupTime(ldt)
            .withPizzaList(List.of(pizza1, pizza2))
            .withCoupon("ABCD12")
            .build();
    }

    @Test
    public void get_all_orders_actual_orders_in_db() {
        Order o1 = new Order();
        when(orderRepository.findAll()).thenReturn(List.of(order, o1));

        Assertions.assertThat(orderService.getAllOrders()).containsExactly(order, o1);
    }

    @Test
    public void get_all_orders_no_orders_in_db() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        Assertions.assertThat(orderService.getAllOrders()).isEmpty();
    }

    @Test
    public void get_orders_for_customer_and_he_has_orders() {
        Order o1 = new Order();
        String netId = "Matt";
        when(orderRepository.findAllByCustomerId(netId)).thenReturn(List.of(order, o1));

        Assertions.assertThat(orderService.getOrdersForCustomer(netId)).containsExactly(order, o1);
    }

    @Test
    public void get_orders_for_customer_but_no_orders() {
        String netId = "Matt";
        when(orderRepository.findAllByCustomerId(netId)).thenReturn(new ArrayList<>());

        Assertions.assertThat(orderService.getOrdersForCustomer(netId)).isEmpty();
    }

    @Test
    public void get_order_by_id_and_it_exists() throws Exception {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void get_order_by_id_and_it_does_not_exist() throws Exception {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            orderService.getOrderById(orderId);
        }).isInstanceOf(OrderNotFoundException.class).hasMessage("1");
    }

    @Test
    public void add_order() {
        when(orderRepository.save(order)).thenReturn(order);
        Order o = orderService.addOrder(order);

        Assertions.assertThat(o).isEqualTo(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void remove_order_which_exists() throws Exception {
        long orderId = 1;
        when(orderRepository.existsById(orderId)).thenReturn(true);

        orderService.removeOrderById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    public void remove_order_which_does_not_exist() throws Exception {
        long orderId = 1;
        when(orderRepository.existsById(orderId)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> {
            orderService.removeOrderById(orderId);
        }).isInstanceOf(OrderNotFoundException.class).hasMessage("1");

        verify(orderRepository, times(0)).deleteById(orderId);
    }

    @Test
    public void get_price_manager() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getPrice(orderId, "Matt", UserRole.REGIONAL_MANAGER)).isEqualTo(32.0);
    }

    @Test
    public void get_price_owner() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getPrice(orderId, "Matt", UserRole.STORE_OWNER)).isEqualTo(32.0);
    }

    @Test
    public void get_price_customer() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getPrice(orderId, "Matt", UserRole.CUSTOMER)).isEqualTo(32.0);
    }

    @Test
    public void get_price_customer_does_not_belong() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThatThrownBy(() -> {
            orderService.getPrice(orderId, "Andrew", UserRole.CUSTOMER);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"Order does not belong to customer, so they cannot check the price\"");
    }

    @Test
    public void get_order_manager() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getOrder(orderId, "Matt", UserRole.REGIONAL_MANAGER)).isEqualTo(order);
    }

    @Test
    public void get_order_owner() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getOrder(orderId, "Matt", UserRole.STORE_OWNER)).isEqualTo(order);
    }

    @Test
    public void get_order_customer() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThat(orderService.getOrder(orderId, "Matt", UserRole.CUSTOMER)).isEqualTo(order);
    }

    @Test
    public void get_order_customer_does_not_belong() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Assertions.assertThatThrownBy(() -> {
            orderService.getOrder(orderId, "Andrew", UserRole.CUSTOMER);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"Order does not belong to customer, so they cannot check it\"");
    }

    @Test
    public void get_all_orders_manager() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        List<Order> list = orderService.getAllOrders("Matt", UserRole.REGIONAL_MANAGER);

        Assertions.assertThat(list).containsExactly(order);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void get_all_orders_owner() {
        Assertions.assertThatThrownBy(() -> {
            orderService.getAllOrders("Matt", UserRole.STORE_OWNER);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("403 FORBIDDEN \"No access\"");
    }

    @Test
    public void get_all_orders_customer() {
        when(orderRepository.findAllByCustomerId("Matt")).thenReturn(List.of(order));
        List<Order> list = orderService.getAllOrders("Matt", UserRole.CUSTOMER);

        Assertions.assertThat(list).containsExactly(order);
        verify(orderRepository, times(1)).findAllByCustomerId("Matt");
    }

    @Test
    public void is_order_removable_manager() {
        Assertions.assertThat(
            orderService.isOrderRemovable("Andrew", "ROLE_REGIONAL_MANAGER", order, "Matt")
        ).isTrue();
    }

    @Test
    public void is_order_removable_owner() {
        Assertions.assertThat(
            orderService.isOrderRemovable("Andrew", "ROLE_STORE_OWNER", order, "Matt")
        ).isFalse();
    }

    @Test
    public void is_order_removable_customer_correct_time_correct_name_and_belongs() {
        order.setPickupTime(LocalDateTime.now().plusMinutes(31));
        when(orderRepository.findAllByCustomerId("Matt")).thenReturn(List.of(order));
        Assertions.assertThat(
            orderService.isOrderRemovable("Matt", "ROLE_CUSTOMER", order, "Matt")
        ).isTrue();
    }

    @Test
    public void is_order_removable_customer_incorrect_time_correct_name_and_belongs() {
        when(orderRepository.findAllByCustomerId("Matt")).thenReturn(List.of(order));
        order.setPickupTime(LocalDateTime.now().minusMinutes(29));
        Assertions.assertThat(
            orderService.isOrderRemovable("Matt", "ROLE_CUSTOMER", order, "Matt")
        ).isFalse();
    }

    @Test
    public void is_order_removable_customer_correct_time_incorrect_name_and_belongs() {
        order.setPickupTime(LocalDateTime.now().plusMinutes(31));
        when(orderRepository.findAllByCustomerId("Matt")).thenReturn(List.of(order));
        Assertions.assertThat(
            orderService.isOrderRemovable("Matt", "ROLE_CUSTOMER", order, "Andrew")
        ).isFalse();
    }

    @Test
    public void is_order_removable_customer_correct_time_correct_name_and_does_not_belong() {
        order.setPickupTime(LocalDateTime.now().plusMinutes(31));
        when(orderRepository.findAllByCustomerId("Matt")).thenReturn(List.of());
        Assertions.assertThat(
            orderService.isOrderRemovable("Matt", "ROLE_CUSTOMER", order, "Matt")
        ).isFalse();
    }

}
