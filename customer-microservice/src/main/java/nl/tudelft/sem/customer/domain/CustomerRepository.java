package nl.tudelft.sem.customer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The Customer Repository.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Saves a customer.
     *
     * @param customer the customer to save
     * @return the saved customer
     */
    Customer save(Customer customer);

    /**
     * Find all customers.
     */
    List<Customer> findAll();

    /**
     * Find customer by Id.
     */
    Customer findById(int customerId);

}



