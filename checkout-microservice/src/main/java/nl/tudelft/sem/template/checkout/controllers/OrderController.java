package nl.tudelft.sem.template.checkout.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.checkout.domain.Order;
import nl.tudelft.sem.template.checkout.domain.OrderBuilder;
import nl.tudelft.sem.template.checkout.domain.OrderService;
import nl.tudelft.sem.template.commons.entity.StoreTimeCoupons;
import nl.tudelft.sem.template.commons.models.CartPizza;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.models.PricesCodesModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.commons.utils.RequestObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final transient AuthManager authManager;
    private final transient RequestHelper requestHelper;
    private final transient OrderService orderService;

    private long getStoreId(String storeName) {
        String storeIdLong =
            requestHelper.doRequest(new RequestObject(HttpMethod.POST, 8084, "/store/getStoreIdFromName"), storeName,
                String.class);
        long storeId = Long.parseLong(storeIdLong);
        if (storeId == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is not a real store");
        }
        return storeId;
    }

    private List<CartPizza> getPizzas() {
        CartPizza[] pizzas =
            requestHelper.doRequest(new RequestObject(HttpMethod.POST, 8082, "/cart/getCart/" + authManager.getNetId()),
                CartPizza[].class);
        return Arrays.asList(pizzas);
    }

    private List<Double> getPriceForEachPizza(List<CartPizza> pizzas) {
        List<Double> priceList = new ArrayList<>(pizzas.size());
        for (CartPizza pizza : pizzas) {
            for (int i = 0; i < pizza.getAmount(); i++) {
                priceList.add(pizza.getPizza().calculatePrice());
            }
        }
        return priceList;
    }


    /**
     * Creates a new order for a customer based on their cart.
     *
     * @param storeTimeCoupons The information about their order
     * @return Response indicating whether it succeeded or failed
     */
    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody StoreTimeCoupons storeTimeCoupons) {
        long storeId;
        try {
            storeId = getStoreId(storeTimeCoupons.getStoreName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        LocalDateTime pickupTime = storeTimeCoupons.getPickupTime();
        if (pickupTime.minusMinutes(30).isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Pickup time should be at least 30 minutes from order placement");
        }
        List<CartPizza> pizzas = getPizzas();
        if (pizzas.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }
        Order order = createOrderFromAttributes(storeTimeCoupons, storeId, pickupTime, pizzas);
        requestHelper.doRequest(new RequestObject(HttpMethod.POST, 8084, "/store/notify"), storeId,
            String.class); // notify store of new order
        return ResponseEntity.ok("Order added with id " + order.getOrderId());
    }

    private Order createOrderFromAttributes(StoreTimeCoupons storeTimeCoupons, long storeId, LocalDateTime pickupTime,
                                            List<CartPizza> pizzas) {
        String customer = authManager.getNetId();
        List<Double> pizzaPrices = getPriceForEachPizza(pizzas);
        List<String> couponCodes = storeTimeCoupons.getCoupons();
        PricesCodesModel pcm = new PricesCodesModel(customer, storeId, pizzaPrices, couponCodes);
        CouponFinalPriceModel finalCoupon =
            requestHelper.doRequest(new RequestObject(HttpMethod.POST, 8085, "/selectCoupon"), pcm,
                CouponFinalPriceModel.class); // get the best coupon
        String finalCouponCode = finalCoupon.getCode();
        OrderBuilder orderBuilder =
            Order.builder().withStoreId(storeId).withCustomerId(customer).withPickupTime(pickupTime).withPizzaList(pizzas)
                .withFinalPrice(finalCoupon.getPrice());
        if (finalCouponCode.isEmpty()) {
            orderBuilder.withCoupon(null);
        } else {
            orderBuilder.withCoupon(finalCouponCode);
            requestHelper.doRequest(new RequestObject(HttpMethod.POST, 8081, "/customers/" + customer + "/coupons/add"),
                finalCouponCode, String.class); // add to customer's used coupons
        }
        return orderService.addOrder(orderBuilder.build());
    }

    /**
     * Removes an order by its order id.
     *
     * @param orderId The id of the order
     * @return Response indicating whether it succeeded in deleting the order
     */
    @PostMapping("/remove/{id}")
    public ResponseEntity<String> removeOrderById(@PathVariable("id") long orderId) {
        String netId = authManager.getNetId();
        String role = authManager.getRoleAuthority();

        try {
            return tryRemoveOrderById(orderId, netId, role);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private ResponseEntity<String> tryRemoveOrderById(long orderId, String netId, String role) throws Exception {
        Order orderToBeRemoved = orderService.getOrderById(orderId);
        String customerId = orderToBeRemoved.getCustomerId();
        long storeId = orderToBeRemoved.getStoreId();
        if (role.equals("ROLE_STORE_OWNER")) {
            return ResponseEntity.badRequest().body("Store owners can't cancel orders");
        }
        if (isRegionalManagerOrOrderIsRemovable(netId, role, orderToBeRemoved, customerId)) {
            orderService.removeOrderById(orderId);
            removeOrderAndCoupon(netId, orderToBeRemoved, storeId);
            return ResponseEntity.ok("Order with id " + orderId + " successfully removed");
        } else {
            return ResponseEntity.badRequest().body(
                "Order does not belong to customer or there are less than 30 minutes until pickup time, "
                    + "so cancelling is not possible");
        }
    }

    private boolean isRegionalManagerOrOrderIsRemovable(String netId, String role, Order orderToBeRemoved,
                                                        String customerId) {
        return role.equals("ROLE_REGIONAL_MANAGER")
            || (customerId.equals(netId) && orderService.getOrdersForCustomer(netId).contains(orderToBeRemoved)
            && !orderToBeRemoved.getPickupTime().minusMinutes(30).isBefore(LocalDateTime.now()));
    }

    private void removeOrderAndCoupon(String netId, Order orderToBeRemoved, long storeId) {
        if (orderToBeRemoved.getCoupon() != null) {
            requestHelper.doRequest(new RequestObject(HttpMethod.POST, 8081, "/customers/" + netId + "/coupons/remove"),
                orderToBeRemoved.getCoupon(), String.class); // remove from customer's used coupons
        }
        requestHelper.doRequest(new RequestObject(HttpMethod.POST, 8084, "/store/notifyRemoveOrder"), storeId,
            String.class); // notify store of remove order
    }

    /**
     * Gets all the orders. If the user is a customer it will return their orders, if it is a regional manager it will return
     * all orders in the system.
     *
     * @return The orders
     */
    @GetMapping(path = {"", "/", "/all"})
    public List<Order> getAllOrders() {
        UserRole role = authManager.getRole();
        if (role == UserRole.REGIONAL_MANAGER) {
            return orderService.getAllOrders();
        } else if (role == UserRole.CUSTOMER) {
            return orderService.getOrdersForCustomer(authManager.getNetId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access");
        }
    }

    /**
     * Gets an order by its order id.
     *
     * @param orderId The order id
     * @return The order
     */
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable("id") long orderId) {
        String netId = authManager.getNetId();
        UserRole role = authManager.getRole();
        try {
            Order order = orderService.getOrderById(orderId);
            if (role == UserRole.REGIONAL_MANAGER || role == UserRole.STORE_OWNER
                || (role == UserRole.CUSTOMER && order.getCustomerId().equals(netId))) {
                return order;
            } else {
                throw new Exception("Order does not belong to customer, so they cannot check it");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Gets the price of a specific order.
     *
     * @param orderId The id of the order
     * @return The price of the order
     */
    @GetMapping("/price/{id}")
    public double getOrderPrice(@PathVariable("id") long orderId) {
        String netId = authManager.getNetId();
        UserRole role = authManager.getRole();
        try {
            Order order = orderService.getOrderById(orderId);
            if (role == UserRole.REGIONAL_MANAGER || role == UserRole.STORE_OWNER
                || (role == UserRole.CUSTOMER && order.getCustomerId().equals(netId))) {
                return order.calculatePriceWithoutDiscount();
            } else {
                throw new Exception("Order does not belong to customer, so they cannot check the price");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
