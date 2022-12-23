package nl.tudelft.sem.template.customer.domain;

import java.util.Optional;
import nl.tudelft.sem.template.authentication.NetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * The Customer Repository.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Find customer by Id.
     */
    Optional<Customer> findById(int customerId);

    /**
     * Find customer by NetId.
     */
    Optional<Customer> findByNetId(NetId netId);

    boolean existsByNetId(NetId netId);
}



