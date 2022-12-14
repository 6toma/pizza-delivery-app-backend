package nl.tudelft.sem.customer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    public void constructNotNull(){
        Customer customer = new Customer(null,null,null);
        assertNotNull(customer);
    }
}