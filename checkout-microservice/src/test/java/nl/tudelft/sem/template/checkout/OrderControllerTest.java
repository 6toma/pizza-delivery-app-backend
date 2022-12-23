package nl.tudelft.sem.template.checkout;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.checkout.controllers.OrderController;
import nl.tudelft.sem.template.checkout.domain.Order;
import nl.tudelft.sem.template.checkout.domain.OrderNotFoundException;
import nl.tudelft.sem.template.checkout.domain.OrderService;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.StoreTimeCoupons;
import nl.tudelft.sem.template.commons.models.CartPizza;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.models.PricesCodesModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderService orderService;
    private AuthManager authManager;
    private RequestHelper requestHelper;
    private Order order;

    private static final String CUSTOMER_ID = "Matt";

    private LocalDateTime ldt;
    private CartPizza pizza1;
    private CartPizza pizza2;

    @BeforeEach
    void setup() {
        orderService = Mockito.mock(OrderService.class);
        authManager = Mockito.mock(AuthManager.class);
        requestHelper = Mockito.mock(RequestHelper.class);
        orderController = new OrderController(authManager, requestHelper, orderService);

        ldt = LocalDateTime.now().plusHours(1);

        CustomPizza cpizza1 = new CustomPizza("Margherita", 11, new ArrayList<>());
        CustomPizza cpizza2 = new CustomPizza("Hawaii", 10.5, new ArrayList<>());

        pizza1 = new CartPizza(cpizza1, 1);
        pizza2 = new CartPizza(cpizza2, 2);

        order = Order.builder().withStoreId(1L).withCustomerId(CUSTOMER_ID).withPickupTime(ldt)
            .withPizzaList(List.of(pizza1, pizza2)).withCoupon("ABCD12").build();
    }

    @Test
    public void get_all_orders_actual_orders_in_db() {
        Order o1 = new Order();
        List<Order> orderList = List.of(o1, order);
        when(orderService.getAllOrders()).thenReturn(orderList);
        when(authManager.getRole()).thenReturn(UserRole.REGIONAL_MANAGER);

        Assertions.assertThat(orderController.getAllOrders()).containsExactly(o1, order);
    }

    @Test
    public void get_all_orders_no_orders_in_db() {
        when(orderService.getAllOrders()).thenReturn(new ArrayList<>());
        when(authManager.getRole()).thenReturn(UserRole.REGIONAL_MANAGER);

        Assertions.assertThat(orderController.getAllOrders()).isEmpty();
    }

    @Test
    public void get_order_by_id_customer_owns_order_in_db() throws Exception {
        when(authManager.getNetId()).thenReturn(CUSTOMER_ID);
        when(authManager.getRole()).thenReturn(UserRole.CUSTOMER);
        when(orderService.getOrdersForCustomer(CUSTOMER_ID)).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void get_order_by_id_customer_owns_order_but_not_in_db() throws Exception {
        when(authManager.getNetId()).thenReturn(CUSTOMER_ID);
        when(authManager.getRole()).thenReturn(UserRole.CUSTOMER);
        when(orderService.getOrdersForCustomer(CUSTOMER_ID)).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderById(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"1\"");
    }

    @Test
    public void get_order_by_id_regional_manager() throws Exception {
        when(authManager.getRole()).thenReturn(UserRole.REGIONAL_MANAGER);

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void get_order_by_id_store_owner() throws Exception {
        when(authManager.getRole()).thenReturn(UserRole.STORE_OWNER);

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderById(orderId)).isEqualTo(order);
    }

    @Test
    public void get_order_by_id_customer_does_not_own_order() throws Exception {
        order.setCustomerId("Not same customer");
        when(authManager.getNetId()).thenReturn(CUSTOMER_ID);
        when(authManager.getRole()).thenReturn(UserRole.CUSTOMER);

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderById(orderId);
        }).isInstanceOf(ResponseStatusException.class)
        .hasMessage("400 BAD_REQUEST \"Order does not belong to customer, so they cannot check it\"");
    }

    @Test
    public void get_order_price_customer_owns_order_in_db() throws Exception {
        when(authManager.getNetId()).thenReturn(CUSTOMER_ID);
        when(authManager.getRole()).thenReturn(UserRole.CUSTOMER);
        when(orderService.getOrdersForCustomer(CUSTOMER_ID)).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderPrice(orderId)).isEqualTo(32);
    }

    @Test
    public void get_order_price_customer_owns_order_but_not_in_db() throws Exception {
        when(authManager.getNetId()).thenReturn(CUSTOMER_ID);
        when(authManager.getRole()).thenReturn(UserRole.CUSTOMER);
        when(orderService.getOrdersForCustomer(CUSTOMER_ID)).thenReturn(List.of(order));

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderPrice(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"1\"");
    }

    @Test
    public void get_order_price_regional_manager() throws Exception {
        when(authManager.getRole()).thenReturn(UserRole.REGIONAL_MANAGER);

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderPrice(orderId)).isEqualTo(32);
    }

    @Test
    public void get_order_price_store_owner() throws Exception {
        when(authManager.getRole()).thenReturn(UserRole.STORE_OWNER);

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThat(orderController.getOrderPrice(orderId)).isEqualTo(32);
    }

    @Test
    public void get_order_price_customer_does_not_own_order() throws Exception {
        order.setCustomerId("Not same customer");
        when(authManager.getNetId()).thenReturn(CUSTOMER_ID);
        when(authManager.getRole()).thenReturn(UserRole.CUSTOMER);

        long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        Assertions.assertThatThrownBy(() -> {
            orderController.getOrderPrice(orderId);
        }).isInstanceOf(ResponseStatusException.class)
        .hasMessage("400 BAD_REQUEST \"Order does not belong to customer, so they cannot check the price\"");
    }

    @Test
    public void add_order_store_not_found() {
        String storeName = "Store does not exist";
        StoreTimeCoupons stc = new StoreTimeCoupons(storeName, ldt, new ArrayList<>());
        when(requestHelper.postRequest(8084, "/store/getStoreIdFromName", storeName, String.class)).thenReturn("-1");

        ResponseEntity<String> response = orderController.addOrder(stc);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo("400 BAD_REQUEST \"This is not a real store\"");

        verify(orderService, never()).addOrder(any());
    }

    @Test
    public void add_order_bad_pickup_time() {
        String storeName = "Delft Dehoven";
        StoreTimeCoupons stc = new StoreTimeCoupons(storeName, LocalDateTime.now(), new ArrayList<>());
        when(requestHelper.postRequest(8084, "/store/getStoreIdFromName", storeName, String.class)).thenReturn("1");

        ResponseEntity<String> response = orderController.addOrder(stc);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody())
            .isEqualTo("Pickup time should be at least 30 minutes from order placement");

        verify(orderService, never()).addOrder(any());
    }

    @Test
    public void add_order_empty_cart() {
        when(requestHelper.postRequest(8084, "/store/getStoreIdFromName", "Delft Dehoven", String.class)).thenReturn("1");
        when(authManager.getNetId()).thenReturn("Matt");
        when(requestHelper.getRequest(8082, "/cart/getCart/" + authManager.getNetId(), CartPizza[].class)).thenReturn(
            new CartPizza[0]);

        StoreTimeCoupons stc = new StoreTimeCoupons("Delft Dehoven", LocalDateTime.now().plusHours(1), new ArrayList<>());
        ResponseEntity<String> response = orderController.addOrder(stc);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo("Cart is empty");

        verify(orderService, never()).addOrder(any());
        verify(requestHelper, never()).postRequest(8084, "/store/notify", 1, String.class);
    }

    @Test
    public void add_order_proper_cart_no_coupon() {
        when(requestHelper.postRequest(8084, "/store/getStoreIdFromName", "Delft Dehoven", String.class)).thenReturn("1");
        when(authManager.getNetId()).thenReturn("Matt");
        when(requestHelper.getRequest(8082, "/cart/getCart/" + authManager.getNetId(), CartPizza[].class)).thenReturn(
            new CartPizza[] {pizza1, pizza2});


        PricesCodesModel pcm = new PricesCodesModel("Matt", 1, List.of(11.0, 10.5, 10.5), new ArrayList<>());
        when(requestHelper.postRequest(8085, "/selectCoupon", pcm, CouponFinalPriceModel.class)).thenReturn(
            new CouponFinalPriceModel("", 32.0));

        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);
        StoreTimeCoupons stc = new StoreTimeCoupons("Delft Dehoven", pickupTime, new ArrayList<>());

        Order order1 = Order.builder().withStoreId(1).withCustomerId("Matt").withPickupTime(pickupTime)
            .withPizzaList(List.of(pizza1, pizza2)).withCoupon(null).withFinalPrice(32.0).build();
        when(orderService.addOrder(order1)).thenReturn(order1);

        ResponseEntity<String> response = orderController.addOrder(stc);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("Order added with id 0");

        verify(orderService, times(1)).addOrder(order1);
        verify(requestHelper, never()).postRequest(eq(8081), eq("/customers/Matt/coupons/add"), any(), eq(String.class));
        verify(requestHelper, times(1)).postRequest(eq(8084), eq("/store/notify"), any(), eq(String.class));
    }

    @Test
    public void add_order_proper_cart_with_1_coupon_used_or_does_not_work() {
        when(requestHelper.postRequest(8084, "/store/getStoreIdFromName", "Delft Dehoven", String.class)).thenReturn("1");
        when(authManager.getNetId()).thenReturn("Matt");
        when(requestHelper.getRequest(8082, "/cart/getCart/" + authManager.getNetId(), CartPizza[].class)).thenReturn(
            new CartPizza[] {pizza1, pizza2});

        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);
        StoreTimeCoupons stc = new StoreTimeCoupons("Delft Dehoven", pickupTime, List.of("ABCD12"));

        PricesCodesModel pcm = new PricesCodesModel("Matt", 1, List.of(11.0, 10.5, 10.5), List.of("ABCD12"));
        when(requestHelper.postRequest(8085, "/selectCoupon", pcm, CouponFinalPriceModel.class)).thenReturn(
            new CouponFinalPriceModel("", 32.0));

        Order order1 = Order.builder().withStoreId(1).withCustomerId("Matt").withPickupTime(pickupTime)
            .withPizzaList(List.of(pizza1, pizza2)).withCoupon(null).withFinalPrice(32.0).build();
        when(orderService.addOrder(order1)).thenReturn(order1);

        ResponseEntity<String> response = orderController.addOrder(stc);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("Order added with id 0");

        verify(orderService, times(1)).addOrder(order1);
        verify(requestHelper, never()).postRequest(eq(8081), eq("/customers/Matt/coupons/add"), any(), eq(String.class));
        verify(requestHelper, times(1)).postRequest(eq(8084), eq("/store/notify"), any(), eq(String.class));
    }

    @Test
    public void add_order_proper_cart_with_2_coupons_and_works() {
        when(requestHelper.postRequest(8084, "/store/getStoreIdFromName", "Delft Dehoven", String.class)).thenReturn("1");
        when(authManager.getNetId()).thenReturn("Matt");
        when(requestHelper.getRequest(8082, "/cart/getCart/" + authManager.getNetId(), CartPizza[].class)).thenReturn(
            new CartPizza[] {pizza1, pizza2});

        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);
        StoreTimeCoupons stc = new StoreTimeCoupons("Delft Dehoven", pickupTime, List.of("ABCD12", "MATT10"));

        PricesCodesModel pcm = new PricesCodesModel("Matt", 1, List.of(11.0, 10.5, 10.5), List.of("ABCD12", "MATT10"));
        when(requestHelper.postRequest(8085, "/selectCoupon", pcm, CouponFinalPriceModel.class)).thenReturn(
            new CouponFinalPriceModel("MATT10", 28.8));

        Order order1 = Order.builder().withStoreId(1).withCustomerId("Matt").withPickupTime(pickupTime)
            .withPizzaList(List.of(pizza1, pizza2)).withCoupon("MATT10").withFinalPrice(28.8).build();
        when(orderService.addOrder(order1)).thenReturn(order1);

        ResponseEntity<String> response = orderController.addOrder(stc);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("Order added with id 0");

        verify(orderService, times(1)).addOrder(order1);
        verify(requestHelper, times(1)).postRequest(eq(8081), eq("/customers/Matt/coupons/add"), any(), eq(String.class));
        verify(requestHelper, times(1)).postRequest(eq(8084), eq("/store/notify"), any(), eq(String.class));
    }

    @Test
    public void remove_order_order_does_not_exist() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRoleAuthority()).thenReturn("ROLE_CUSTOMER");

        long orderId = 1;
        when(orderService.getOrderById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        Assertions.assertThatThrownBy(() -> {
            orderController.removeOrderById(orderId);
        }).isInstanceOf(ResponseStatusException.class).hasMessage("400 BAD_REQUEST \"1\"");

        verify(orderService, never()).removeOrderById(orderId);
    }

    @Test
    public void remove_order_store_owner() throws Exception {
        when(authManager.getNetId()).thenReturn("Matt");
        when(authManager.getRoleAuthority()).thenReturn("ROLE_STORE_OWNER");

        long orderId = 1;
        when(orderService.getOrderById(orderId)).thenReturn(order);

        ResponseEntity<String> response = orderController.removeOrderById(orderId);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo("Store owners can't cancel orders");

        verify(orderService, never()).removeOrderById(orderId);
    }

    @Test
    public void remove_order_regional_manager_no_coupon() throws Exception {
        when(authManager.getRoleAuthority()).thenReturn("ROLE_REGIONAL_MANAGER");

        long orderId = 1;
        order.setOrderId(1);
        order.setCoupon(null);
        when(orderService.getOrderById(orderId)).thenReturn(order);

        ResponseEntity<String> response = orderController.removeOrderById(orderId);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("Order with id 1 successfully removed");

        verify(orderService, times(1)).removeOrderById(orderId);
        verify(requestHelper, never()).postRequest(eq(8081), eq("/customers/Matt/coupons/remove"), any(), eq(String.class));
    }

    @Test
    public void remove_order_customer_does_not_own_it_1() throws Exception {
        when(authManager.getRoleAuthority()).thenReturn("ROLE_CUSTOMER");
        when(authManager.getNetId()).thenReturn("Matt");

        long orderId = 1;
        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(new ArrayList<>());

        ResponseEntity<String> response = orderController.removeOrderById(orderId);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo("Order does not belong to customer or there are "
            + "less than 30 minutes until pickup time, so cancelling is not possible");

        verify(orderService, never()).removeOrderById(orderId);
    }

    @Test
    public void remove_order_customer_does_not_own_it_2() throws Exception {
        when(authManager.getRoleAuthority()).thenReturn("ROLE_CUSTOMER");
        when(authManager.getNetId()).thenReturn("Andy");

        long orderId = 1;
        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(List.of(order));

        ResponseEntity<String> response = orderController.removeOrderById(orderId);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo(
            "Order does not belong to customer or there are less than 30 minutes until pickup time, "
                + "so cancelling is not possible");

        verify(orderService, never()).removeOrderById(orderId);
    }

    @Test
    public void remove_order_customer_owns_it_but_too_late_to_cancel() throws Exception {
        when(authManager.getRoleAuthority()).thenReturn("ROLE_CUSTOMER");
        when(authManager.getNetId()).thenReturn("Matt");

        long orderId = 1;
        order.setPickupTime(LocalDateTime.now().plusMinutes(29));
        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(List.of(order));

        ResponseEntity<String> response = orderController.removeOrderById(orderId);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo(
            "Order does not belong to customer or there are less than 30 minutes until pickup time, "
                + "so cancelling is not possible");

        verify(orderService, never()).removeOrderById(orderId);
    }

    @Test
    public void remove_order_customer_owns_it_has_coupon_and_cancel() throws Exception {
        when(authManager.getRoleAuthority()).thenReturn("ROLE_CUSTOMER");
        when(authManager.getNetId()).thenReturn("Matt");

        long orderId = 1;
        order.setOrderId(1);
        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(orderService.getOrdersForCustomer("Matt")).thenReturn(List.of(order));

        ResponseEntity<String> response = orderController.removeOrderById(orderId);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("Order with id 1 successfully removed");

        verify(orderService, times(1)).removeOrderById(orderId);
        verify(requestHelper, times(1)).postRequest(eq(8081), eq("/customers/Matt/coupons/remove"), any(), eq(String.class));
    }
}
