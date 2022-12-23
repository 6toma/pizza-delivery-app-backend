package nl.tudelft.sem.template.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.customer.domain.Customer;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    public void constructNotNull(){
        NetId netId = new NetId("example123@test.com");
        Customer customer = new Customer(netId);
        assertNotNull(customer);
        assertTrue(customer.getUsedCoupons().isEmpty());
        assertTrue(customer.getAllergens().isEmpty());
    }

    @Test
    public void testCustomer() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");

        Customer customer = new Customer(netId);
        customer.setCustomerId(customerId);
        customer.setAllergens(allergens);
        customer.setUsedCoupons(usedCoupons);

        assertNotNull(customer);
        assertEquals(usedCoupons, customer.getUsedCoupons());
        assertEquals(allergens, customer.getAllergens());
        assertEquals(customerId, customer.getCustomerId());
        assertEquals(netId, customer.getNetId());
    }

    @Test
    public void testSetUsedCoupons() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");

        Customer customer = new Customer(netId);
            customer.setCustomerId(customerId);
            customer.setAllergens(allergens);
            customer.setUsedCoupons(usedCoupons);

        assertEquals(usedCoupons, customer.getUsedCoupons());

        List<String> newUsedCoupons = Arrays.asList("coupon3", "coupon4");
        customer.setUsedCoupons(newUsedCoupons);
        assertEquals(newUsedCoupons, customer.getUsedCoupons());
    }

    @Test
    public void testSetAllergens() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");

        Customer customer = new Customer(netId);
            customer.setCustomerId(customerId);
            customer.setAllergens(allergens);
            customer.setUsedCoupons(usedCoupons);

        assertEquals(allergens, customer.getAllergens());

        List<String> newAllergens = Arrays.asList("dairy", "egg");
        customer.setAllergens(newAllergens);
        assertEquals(newAllergens, customer.getAllergens());
    }

    @Test
    public void testSetCustomerId() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");

        Customer customer = new Customer(netId);
            customer.setCustomerId(customerId);
            customer.setAllergens(allergens);
            customer.setUsedCoupons(usedCoupons);

        assertEquals(customerId, customer.getCustomerId());

        int newCustomerId = 654321;
        customer.setCustomerId(newCustomerId);
        assertEquals(newCustomerId, customer.getCustomerId());
    }

    @Test
    public void testGetCustomerId() {
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");
        Customer customer = new Customer(netId);
        customer.setCustomerId(customerId);
        assertEquals(customerId, customer.getCustomerId());
    }

    @Test
    public void testSetNetId() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");

        Customer customer = new Customer(netId);
            customer.setCustomerId(customerId);
            customer.setAllergens(allergens);
            customer.setUsedCoupons(usedCoupons);
        assertEquals(netId, customer.getNetId());

        NetId newNetId = new NetId("example456@test.com");
        customer.setNetId(newNetId);
        assertEquals(newNetId, customer.getNetId());
    }

    @Test
    public void testGetNetId() {
        NetId netId = new NetId("example123@test.com");
        Customer customer = new Customer(netId);
        assertEquals(netId, customer.getNetId());
    }

    @Test
    public void testGetUsedCoupons() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        NetId netId = new NetId("example123@test.com");
        Customer customer = new Customer(netId);
        customer.setUsedCoupons(usedCoupons);
        assertEquals(usedCoupons, customer.getUsedCoupons());
    }

    @Test
    public void testGetAllergens() {
        List<String> allergens = Arrays.asList("peanut", "gluten");
        NetId netId = new NetId("example123@test.com");
        Customer customer = new Customer(netId);
        customer.setAllergens(allergens);
        assertEquals(allergens, customer.getAllergens());
    }
}