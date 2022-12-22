package nl.tudelft.sem.customer.domain;

import java.util.Optional;
import nl.tudelft.sem.template.authentication.UserEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * The Customer Repository.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByCustomerId(int customerId);

    /**
     * Find customer by UserEmail.
     */
    Customer findByEmail(UserEmail customerId);

}



