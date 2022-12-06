package nl.tudelft.sem.template.example;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for pizzas.
 */
@Repository
public interface PizzaRepository extends JpaRepository<Pizza, String> {

    /**
     * Saves a pizza.
     *
     * @param pizza must not be {@literal null}.
     * @return the saved pizza
     */
    Pizza save(Pizza pizza);

    /**
     * Find all pizzas.
     */
    List<Pizza> findAll();

    /**
     * Find pizza by name.
     */
    void deleteById(String pizzaName);

    /**
     * Check if a pizza already exists with this name.
     */
    boolean existsByPizzaName(String pizzaName);
}
