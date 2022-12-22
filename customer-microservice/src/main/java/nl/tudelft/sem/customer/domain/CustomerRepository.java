package nl.tudelft.sem.customer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nl.tudelft.sem.template.authentication.NetId;


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
    Customer findByNetId(NetId customerId);

}



