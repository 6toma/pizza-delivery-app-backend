package nl.tudelft.sem.checkout.domain;

import nl.tudelft.sem.checkout.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order save(Order order);

    List<Order> findAll();

    @Query(
        value = "SELECT * FROM orders o WHERE o.customerId = ?1",
        nativeQuery = true)
    List<Order> findOrdersForCustomer(String netId);
}
