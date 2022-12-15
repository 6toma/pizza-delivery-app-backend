package nl.tudelft.sem.template.cart;

import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A repository for pizzas.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, String> {


}
