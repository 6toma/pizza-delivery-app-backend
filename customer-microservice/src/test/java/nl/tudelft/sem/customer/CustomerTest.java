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
        Customer customer = new Customer(netId);
        assertNotNull(customer);
    }

    @Test
    public void testCustomer() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123");

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
        NetId netId = new NetId("example123");

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
        NetId netId = new NetId("example123");

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
        NetId netId = new NetId("example123");

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
    public void testSetNetId() {
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123");

        Customer customer = new Customer(netId);
            customer.setCustomerId(customerId);
            customer.setAllergens(allergens);
            customer.setUsedCoupons(usedCoupons);
        assertEquals(netId, customer.getNetId());

        NetId newNetId = new NetId("example456");
        customer.setNetId(newNetId);
        assertEquals(newNetId, customer.getNetId());
    }
}