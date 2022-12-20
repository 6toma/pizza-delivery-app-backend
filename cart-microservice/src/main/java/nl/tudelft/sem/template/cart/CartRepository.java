package nl.tudelft.sem.template.cart;

import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.commons.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A repository for pizzas.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Cart findByNetId(NetId netId);

    @Transactional
    void deleteByNetId(NetId netId);
}
