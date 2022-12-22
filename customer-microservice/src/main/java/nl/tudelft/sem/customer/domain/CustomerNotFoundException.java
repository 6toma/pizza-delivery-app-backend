package nl.tudelft.sem.customer.domain;

import nl.tudelft.sem.template.authentication.UserEmail;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(int customerId) {

        super("Customer with ID " + customerId + " not found");
    }
    public CustomerNotFoundException(UserEmail email) {
        super("Customer with NetID " + email + " not found");
    }
}