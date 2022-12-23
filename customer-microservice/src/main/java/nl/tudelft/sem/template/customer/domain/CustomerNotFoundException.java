package nl.tudelft.sem.template.customer.domain;

import nl.tudelft.sem.template.authentication.NetId;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(int customerId) {

        super("Customer with ID " + customerId + " not found");
    }
    public CustomerNotFoundException(NetId netId) {
        super("Customer with NetID " + netId + " not found");
    }
}