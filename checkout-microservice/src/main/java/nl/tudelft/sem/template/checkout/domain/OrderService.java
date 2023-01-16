package nl.tudelft.sem.template.checkout.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class OrderService {


    private final transient OrderRepository orderRepository;

    /**
     * Retrieves all orders from the DB.
     *
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersForCustomer(String netId) {
        return orderRepository.findAllByCustomerId(netId);
    }

    /**
     * Retrieves an order based on the order id.
     *
     * @param orderId the id of the order to be returned
     * @return the order with the id specified in the parameter
     * @throws Exception if the order with the given ID does not exist in the repository
     */
    public Order getOrderById(long orderId) throws Exception {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        }
        throw new OrderNotFoundException(orderId);
    }

    /**
     * Adds an order to the DB.
     *
     * @param order - the Order object to add
     */
    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Removes an order from the DB.
     *
     * @param orderId - the id of the Order object to be removed
     */
    public void removeOrderById(long orderId) throws Exception {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
        } else {
            throw new OrderNotFoundException(orderId);
        }
    }

    public double getPrice(long orderId, String netId, UserRole role) {
        try {
            Order order = getOrderById(orderId);
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

    public Order getOrder(long orderId, String netId, UserRole role) {
        try {
            Order order = getOrderById(orderId);
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

    public List<Order> getAllOrders(String netId, UserRole role) {
        if (role == UserRole.REGIONAL_MANAGER) {
            return getAllOrders();
        } else if (role == UserRole.CUSTOMER) {
            return getOrdersForCustomer(netId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access");
        }
    }

    public boolean isOrderRemovable(String netId, String role, Order orderToBeRemoved, String customerId) {
        return role.equals("ROLE_REGIONAL_MANAGER")
            || (customerId.equals(netId) && getOrdersForCustomer(netId).contains(orderToBeRemoved)
            && !orderToBeRemoved.getPickupTime().minusMinutes(30).isBefore(LocalDateTime.now()));
    }

}
