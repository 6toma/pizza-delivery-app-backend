package nl.tudelft.sem.template.checkout;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Rest controller for orders.
 */
@RestController
public class OrderController {

    private final transient OrderService orderService;

    /**
     * Creates a new order controller.
     *
     * @param orderService The order service
     */
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Adds an order.
     *
     * @param order The order to add
     * @return The response
     */
    @PostMapping("/orders/add")
    public ResponseEntity<String> addOrder(@RequestBody OrderModel order) {
        Order newOrder = new Order(order.getStoreId(), order.getPizzaList());
        orderService.addOrder(newOrder);
        return ResponseEntity.ok("Order added");
    }

    /**
     * Removes an order by its id.
     *
     * @param orderId The id of the order
     * @return The response
     */
    @PostMapping("/orders/remove")
    public ResponseEntity<String> removeOrder(@RequestBody int orderId) {
        orderService.removeOrderById(orderId);
        return ResponseEntity.ok("Order removed");
    }

    /**
     * Retrieves all orders.
     *
     * @return All orders
     */
    @GetMapping("/orders/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Retrieves the price of an order.
     *
     * @param orderId The id of the order
     * @return The price of the order
     */
    @GetMapping("/orders/price")
    public int getOrderPrice(@RequestBody int orderId) {
        try {
            return orderService.getOrderById(orderId).calculatePrice();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
