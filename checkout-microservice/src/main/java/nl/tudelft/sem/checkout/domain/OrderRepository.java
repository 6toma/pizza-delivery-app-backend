package nl.tudelft.sem.checkout.domain;

import nl.tudelft.sem.checkout.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Order save(Order order);

    List<Order> findAll();

    Optional<Order> findByOrderId(long orderId);

    boolean existsByOrderId(long orderId);

    void deleteOrderByOrderId(long orderId);

}
