package nl.tudelft.sem.template.cart;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for pizzas.
 */
@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
    /**
     * Find pizza by name.
     */
    void deleteByPizzaName(String pizzaName);

    /**
     * Check if a pizza already exists with this name.
     */
    boolean existsByPizzaName(String pizzaName);

    Optional<Pizza> findByPizzaName(String pizzaName);
}
