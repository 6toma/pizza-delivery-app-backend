package nl.tudelft.sem.template.cart;

import java.util.Optional;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for toppings.
 */
@Repository
public interface ToppingRepository extends JpaRepository<Topping, Integer> {

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
