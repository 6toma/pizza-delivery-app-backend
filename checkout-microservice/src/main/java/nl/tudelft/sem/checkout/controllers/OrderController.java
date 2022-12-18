package nl.tudelft.sem.checkout.controllers;

import java.util.Collections;
import nl.tudelft.sem.checkout.domain.Order;
import nl.tudelft.sem.checkout.domain.OrderModel;
import nl.tudelft.sem.checkout.domain.OrderService;
import nl.tudelft.sem.template.authentication.annotations.role.RoleRegionalManager;
import nl.tudelft.sem.template.commons.entity.Pizza;
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

    private final transient OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
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
        orderService.removeOrderById(orderId);
        return ResponseEntity.ok("Order removed");
    }

    @RoleRegionalManager
    @GetMapping(path = {"", "/", "/all"})
    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable("id") long orderId) throws Exception {
        try {
            return orderService.getOrderById(orderId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

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
        try {
            return orderService.getOrderById(orderId).calculatePriceWithoutDiscount();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
