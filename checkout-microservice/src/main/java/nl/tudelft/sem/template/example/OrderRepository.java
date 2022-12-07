package nl.tudelft.sem.template.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Order save(Order order);

    List<Order> findAll();

    Optional<Order> findByOrderId(int orderId);

    boolean existsByOrderId(int orderId);

    void deleteOrderByOrderId(int orderId);

}
