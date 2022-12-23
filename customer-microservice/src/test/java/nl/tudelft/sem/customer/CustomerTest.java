package nl.tudelft.sem.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.customer.domain.Customer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import nl.tudelft.sem.template.authentication.NetId;

class CustomerTest {

    @Test
    public void constructNotNull(){
        NetId netId = new NetId("example123");
        Customer customer = new Customer(null,null,0, netId);
        assertNotNull(customer);
    }

    @Test
    public void testCustomer() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123");

        Customer customer = new Customer(usedCoupons, allergens, customerId, netId);

        assertNotNull(customer);
        assertEquals(usedCoupons, customer.getUsedCoupons());
        assertEquals(allergens, customer.getAllergens());
        assertEquals(customerId, customer.getCustomerId());
        assertEquals(netId, customer.getNetId());
    }
}