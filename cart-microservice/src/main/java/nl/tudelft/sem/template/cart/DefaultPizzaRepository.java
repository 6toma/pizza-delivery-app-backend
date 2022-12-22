package nl.tudelft.sem.template.cart;

import java.util.Optional;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for pizzas.
 */
@Repository
public interface DefaultPizzaRepository extends JpaRepository<DefaultPizza, Integer> {
    /**
     * Find pizza by name.
     */
    void deleteByPizzaName(String pizzaName);

    /**
     * Check if a pizza already exists with this name.
     */
    boolean existsByPizzaName(String pizzaName);

    Optional<DefaultPizza> findByPizzaName(String pizzaName);
}
