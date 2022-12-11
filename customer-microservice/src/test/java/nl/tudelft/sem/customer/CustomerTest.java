package nl.tudelft.sem.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.customer.domain.Customer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class CustomerTest {

    @Test
    public void constructNotNull(){
        Customer customer = new Customer(null,null,0);
        assertNotNull(customer);
    }

    @Test
    void testCustomer() {
        // Create a list of used coupons
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");

        // Create a list of allergens
        List<String> allergens = Arrays.asList("gluten", "peanuts");

        // Create a customer with the specified used coupons and allergens
        Customer customer = new Customer(usedCoupons, allergens, 1);

        // Assert that the customer's used coupons and allergens are as expected
        assertEquals(usedCoupons, customer.getUsedCoupons());
        assertEquals(allergens, customer.getAllergens());

        // Assert that the customer's ID is as expected
        assertEquals(1, customer.getCustomerId());
    }
}