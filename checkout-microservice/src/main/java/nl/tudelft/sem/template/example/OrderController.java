package nl.tudelft.sem.template.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final transient OrderService orderService;

    @PostMapping("/orders/add")
    public ResponseEntity<String> addOrder(@RequestBody OrderModel order) {
        Order newOrder = new Order(order.getStoreId(), order.getPizzaList());
        orderService.addOrder(newOrder);
        return ResponseEntity.ok("Order added");
    }

    @PostMapping("/orders/remove")
    public ResponseEntity<String> removeOrder(@RequestBody int orderId) {
        orderService.removeOrderById(orderId);
        return ResponseEntity.ok("Order removed");
    }

    @GetMapping("/orders/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/price")
    public int getOrderPrice(@RequestBody int orderId) throws Exception {
        try {
            return orderService.getOrderById(orderId).calculatePrice();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
