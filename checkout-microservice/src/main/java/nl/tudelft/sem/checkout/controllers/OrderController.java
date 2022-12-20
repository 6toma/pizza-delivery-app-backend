package nl.tudelft.sem.checkout.controllers;

import java.util.Collections;
import nl.tudelft.sem.checkout.domain.Order;
import nl.tudelft.sem.checkout.domain.OrderModel;
import nl.tudelft.sem.checkout.domain.OrderService;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.annotations.role.RoleRegionalManager;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody OrderModel order) {
        Order newOrder = Order.builder()
            .storeId(order.getStoreId())
            .customerId(order.getCustomerId())
            .pickupTime(order.getPickupTime())
            .coupon(order.getCoupon())
            .build();
        orderService.addOrder(newOrder);
        return ResponseEntity.ok("Order added");
    }

    @PostMapping("/remove/{id}")
    public ResponseEntity<String> removeOrderById(@PathVariable("id") long orderId) {
        String netId = authManager.getNetId();
        String role = authManager.getRole();
        try {
            Order orderToBeRemoved = orderService.getOrderById(orderId);
            if(role.equals("ROLE_STORE_OWNER"))
                return ResponseEntity.badRequest().body("Store owners can't cancel orders");
            if(role.equals("ROLE_REGIONAL_MANGER") || orderService.getOrdersForCustomer(netId).contains(orderToBeRemoved)) {
                orderService.removeOrderById(orderId);
                return ResponseEntity.ok("Order removed");
            }
            else return ResponseEntity.badRequest().body("Order does not belong to customer, so they cannot cancel it");
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
        String netId = authManager.getNetId();
        String role = authManager.getRole();
        try {
            Order order = orderService.getOrderById(orderId);
            if(role.equals("ROLE_REGIONAL_MANAGER") || role.equals("ROLE_STORE_OWNER") ||
                (role.equals("ROLE_CUSTOMER") && orderService.getOrdersForCustomer(netId).contains(order)))
              return order;
            else throw new Exception("Order does not belong to customer, so they cannot check it");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // This is strictly for interactions
    @GetMapping("/pizza_prices/{id}")
    public List<Double> getPriceForEachPizza(@PathVariable("id") long orderId) throws Exception {
        try {
            Order order = orderService.getOrderById(orderId);

            // list is unsorted, faster to search for min, than to sort and get first value
            List<Double> priceList = new ArrayList<>();
            for(Pizza pizza : order.getPizzaList())
                priceList.add(pizza.getPrice());

            return priceList;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/price/{id}")
    public double getOrderPrice(@PathVariable("id") long orderId) throws Exception {
        String netId = authManager.getNetId();
        String role = authManager.getRole();
        try {
            Order order = orderService.getOrderById(orderId);
            if(role.equals("ROLE_REGIONAL_MANAGER") || role.equals("ROLE_STORE_OWNER") ||
                (role.equals("ROLE_CUSTOMER") && orderService.getOrdersForCustomer(netId).contains(order)))
                return order.calculatePriceWithoutDiscount();
            else throw new Exception("Order does not belong to customer, so they cannot check the price");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
