package nl.tudelft.sem.template.checkout.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order save(Order order);

    List<Order> findAll();

    @Query(
        value = "SELECT * FROM orders o WHERE o.customerId = ?1",
        nativeQuery = true)
    List<Order> findOrdersForCustomer(String email);
}
