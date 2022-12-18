package nl.tudelft.sem.template.cart;

import nl.tudelft.sem.template.commons.entity.Topping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A repository for toppings.
 */
@Repository
public interface ToppingRepository extends JpaRepository<Topping, String> {

    /**
     * Saves a topping.
     *
     * @param topping must not be {@literal null}.
     * @return the saved pizza
     */
    Topping save(Topping topping);

    /**
     * Find all pizzas.
     */
    List<Topping> findAll();

    /**
     * Find topping by name.
     */
    void deleteById(String name);

    /**
     * Find topping by name.
     */
    Optional<Topping> findByName(String name);

    /**
     * Check if a topping already exists with this name.
     */
    boolean existsByName(String name);
}
