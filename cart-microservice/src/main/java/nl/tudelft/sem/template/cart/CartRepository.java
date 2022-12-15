package nl.tudelft.sem.template.cart;

import java.util.List;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
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
