package nl.tudelft.sem.checkout.domain;

import java.util.Optional;
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

    public List<Order> getOrdersForCustomer(String netId) {
        return orderRepository.findOrdersForCustomer(netId);
    }

    /**
     * Retrieves an order based on the order id
     * @param orderId the id of the order to be returned
     * @return the order with the id specified in the parameter
     * @throws Exception if the order with the given ID does not exist in the repository
     */
    public Order getOrderById(long orderId) throws Exception{
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent())
            return order.get();
        throw new OrderNotFoundException(orderId);
    }

    /**
     * Adds an order to the DB
     * @param order - the Order object to add
     */
    public void addOrder(Order order) {
        orderRepository.save(order);
    }
//
//    /**
//     * Removes an order from the DB
//     * @param order - the Order object to be removed
//     */
//    public void removeOrder(Order order) {
//        orderRepository.deleteOrderByOrderId(order.getOrderId());
//    }

    /**
     * Removes an order from the DB
     * @param orderId - the id of the Order object to be removed
     */
    public void removeOrderById(long orderId) throws Exception{
        if(orderRepository.existsById(orderId))
            orderRepository.deleteById(orderId);
        else throw new OrderNotFoundException(orderId);
    }

}
