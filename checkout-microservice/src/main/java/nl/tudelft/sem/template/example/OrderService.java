package nl.tudelft.sem.template.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final transient OrderRepository orderRepository;

    /**
     * Retrieves all orders from the DB
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int orderId) throws Exception{
        if(orderRepository.existsByOrderId(orderId))
            return orderRepository.findByOrderId(orderId).get();
        throw new OrderNotFoundException(orderId);
    }

    /**
     * Adds an order to the DB
     * @param order - the Order object to add
     */
    public void addOrder(Order order) {
        orderRepository.save(order);
    }

    /**
     * Removes an order from the DB
     * @param order - the Order object to be removed
     */
    public void removeOrder(Order order) {
        orderRepository.deleteOrderByOrderId(order.getOrderId());
    }

    /**
     * Removes an order from the DB
     * @param orderId - the id of the Order object to be removed
     */
    public void removeOrderById(int orderId) {
        orderRepository.deleteOrderByOrderId(orderId);
    }

}
