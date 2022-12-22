package nl.tudelft.sem.customer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nl.tudelft.sem.template.authentication.NetId;

import java.util.Optional;


/**
 * The Customer Repository.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Find customer by Id.
     */
    Customer findById(int customerId);

    /**
     * Find customer by NetId.
     */
    Optional<Customer> findByNetId(NetId netId);

    boolean existsByNetId(NetId netId);
}



