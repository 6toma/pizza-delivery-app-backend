package nl.tudelft.sem.template.checkout.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.annotations.role.RoleRegionalManager;
import nl.tudelft.sem.template.checkout.domain.Order;
import nl.tudelft.sem.template.checkout.domain.OrderService;
import nl.tudelft.sem.template.commons.entity.StoreTimeCoupons;
import nl.tudelft.sem.template.commons.models.CartPizza;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.models.PricesCodesModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrderController {

    private final transient AuthManager authManager;
    private final transient RequestHelper requestHelper;
    private final transient OrderService orderService;

    @Autowired
    public OrderController(AuthManager authManager, RequestHelper requestHelper, OrderService orderService) {
        this.authManager = authManager;
        this.requestHelper = requestHelper;
        this.orderService = orderService;
    }

    public long getStoreId(String storeName) throws Exception {
        String storeIdLong = requestHelper.postRequest(8089, "/store/getStoreIdFromName", storeName, String.class);
        long storeId = Long.parseLong(storeIdLong);
        if (storeId == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is not a real store");
        }
        return storeId;
    }

    public List<CartPizza> getPizzas() {
        CartPizza[] pizzas = requestHelper.getRequest(8082, "/cart/getCart/" + authManager.getEmail(), CartPizza[].class);
        return Arrays.asList(pizzas);
    }

    public List<Double> getPriceForEachPizza(List<CartPizza> pizzas) {
        List<Double> priceList = new ArrayList<>(pizzas.size());
        for (CartPizza pizza : pizzas) {
            for (int i = 0; i < pizza.getAmount(); i++) {
                priceList.add(pizza.getPizza().getPrice());
            }
        }
        return priceList;
    }


    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody StoreTimeCoupons storeTimeCoupons) {
        long storeId;
        try {
            storeId = getStoreId(storeTimeCoupons.getStoreName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("some error");
        }
        LocalDateTime pickupTime = storeTimeCoupons.getPickupTime();
        if (pickupTime.minusMinutes(30).isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Pickup time should be at least 30 minutes from order placement");
        }

        List<CartPizza> pizzas = getPizzas();
        String customer = authManager.getEmail();

        List<String> couponCodes = storeTimeCoupons.getCoupons();
        List<Double> pizzaPrices = getPriceForEachPizza(pizzas);
        PricesCodesModel pcm = new PricesCodesModel(pizzaPrices, couponCodes);
        CouponFinalPriceModel finalCoupon =
            requestHelper.postRequest(8085, "/selectCoupon", pcm, CouponFinalPriceModel.class); // get the best coupon
        String finalCouponCode = finalCoupon.getCode();
        Order order = Order.builder()
            .withStoreId(storeId)
            .withCustomerId(customer)
            .withPickupTime(pickupTime)
            .withPizzaList(pizzas)
            .withCoupon(finalCouponCode)
            .build();
        order = orderService.addOrder(order);
        requestHelper.postRequest(8081, "/customers/" + customer + "/coupons/add", finalCouponCode,
            String.class); // add to customer's used coupons
        requestHelper.postRequest(8089, "/store/notify", storeId, String.class); // notify store of new order
        return ResponseEntity.ok("Order added with id " + order.getOrderId());
    }

    @PostMapping("/remove/{id}")
    public ResponseEntity<String> removeOrderById(@PathVariable("id") long orderId) {
        String email = authManager.getEmail();
        String role = authManager.getRole();
        try {
            Order orderToBeRemoved = orderService.getOrderById(orderId);
            if (role.equals("ROLE_STORE_OWNER")) {
                return ResponseEntity.badRequest().body("Store owners can't cancel orders");
            }
            if (role.equals("ROLE_REGIONAL_MANGER") ||
                (orderService.getOrdersForCustomer(email).contains(orderToBeRemoved) &&
                    !orderToBeRemoved.getPickupTime().minusMinutes(30).isBefore(LocalDateTime.now()))) {
                orderService.removeOrderById(orderId);
                requestHelper.postRequest(8081, "/customers/" + email + "/coupons/remove", orderToBeRemoved.getCoupon(),
                    String.class); // remove from customer's used coupons
                return ResponseEntity.ok("Order with id " + orderId + " successfully removed");
            } else {
                return ResponseEntity.badRequest().body(
                    "Order does not belong to customer or there are less than 30 minutes until pickup time, so cancelling is not possible");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RoleRegionalManager
    @GetMapping(path = {"", "/", "/all"})
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable("id") long orderId) throws Exception {
        String email = authManager.getEmail();
        String role = authManager.getRole();
        try {
            Order order = orderService.getOrderById(orderId);
            if (role.equals("ROLE_REGIONAL_MANAGER") || role.equals("ROLE_STORE_OWNER") ||
                (role.equals("ROLE_CUSTOMER") && orderService.getOrdersForCustomer(email).contains(order))) {
                return order;
            } else {
                throw new Exception("Order does not belong to customer, so they cannot check it");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/price/{id}")
    public double getOrderPrice(@PathVariable("id") long orderId) throws Exception {
        String email = authManager.getEmail();
        String role = authManager.getRole();
        try {
            Order order = orderService.getOrderById(orderId);
            if (role.equals("ROLE_REGIONAL_MANAGER") || role.equals("ROLE_STORE_OWNER") ||
                (role.equals("ROLE_CUSTOMER") && orderService.getOrdersForCustomer(email).contains(order))) {
                return order.calculatePriceWithoutDiscount();
            } else {
                throw new Exception("Order does not belong to customer, so they cannot check the price");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
