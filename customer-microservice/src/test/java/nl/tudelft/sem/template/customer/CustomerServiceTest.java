package nl.tudelft.sem.template.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.customer.domain.Customer;
import nl.tudelft.sem.template.customer.domain.CustomerNotFoundException;
import nl.tudelft.sem.template.customer.domain.CustomerRepository;
import nl.tudelft.sem.template.customer.domain.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CustomerServiceTest {

    private CustomerService customerService;
    private AuthManager authManager;
    private Customer customer;
    private CustomerRepository repo;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(CustomerRepository.class);
        authManager = Mockito.mock(AuthManager.class);

        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");
        customer = new Customer(netId);
        customer.setCustomerId(customerId);
        customer.setAllergens(allergens);
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        customer.setUsedCoupons(usedCoupons);
        when(repo.findByNetId(new NetId("example123@test.com"))).thenReturn(java.util.Optional.ofNullable(customer));
        customerService = new CustomerService(repo);
    }

    @Test
    void testGetCustomerByIdSuccess() {
        when(repo.findById(123456)).thenReturn(java.util.Optional.ofNullable(customer));
        assertEquals(customerService.getCustomerById(123456), customer);
    }

    @Test
    void testGetCustomerByIdException() {
        when(repo.findById(123456)).thenReturn(java.util.Optional.ofNullable(null));
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(123456);
        });
    }

    @Test
    void testGetCustomerByNetIdSuccess() {
        assertEquals(customerService.getCustomerByNetId(new NetId("example123@test.com")), customer);
    }

    @Test
    void testGetCustomerByNetIdException() {
        when(repo.findByNetId(new NetId("example123@test.com"))).thenReturn(java.util.Optional.ofNullable(null));
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerByNetId(new NetId("example123@test.com"));
        });
    }

    @Test
    void testGetAll() {
        when(repo.findAll()).thenReturn(List.of(customer));
        assertEquals(customerService.getAll(), List.of(customer));
    }

    @Test
    void testAddCustomer() {
        when(repo.save(customer)).thenReturn(customer);
        assertEquals(customerService.addCustomer(customer), customer);
    }

    @Test
    void testAddToUsedCouponsSuccess() {
        String coupon = "coupon3";
        customerService.addToUsedCoupons(customer.getNetId(), coupon);
        List<String> coupons = new ArrayList<>(customer.getUsedCoupons());
        coupons.add(coupon);
        customer.setUsedCoupons(coupons);
        verify(repo).save(customer);
    }

    @Test
    void testRemoveFromUsedCouponsSuccess() {
        String coupon = "coupon2";
        customerService.removeFromUsedCoupons(customer.getNetId(), coupon);
        List<String> coupons = Arrays.asList("coupon1");
        customer.setUsedCoupons(coupons);
        verify(repo).save(customer);
    }

    @Test
    void testHasUsedCouponsTrue() {
        String coupon = "coupon2";
        assertTrue(customerService.hasUsedCoupon(new NetId("example123@test.com"), coupon));
    }

    @Test
    void testHasUsedCouponsFalse() {
        String coupon = "coupon3";
        assertFalse(customerService.hasUsedCoupon(new NetId("example123@test.com"), coupon));
    }

    @Test
    void testCheckUsedCoupons() {
        List<String> res = List.of("coupon3");
        List<String> coupons = Arrays.asList("coupon1", "coupon3");
        assertEquals(customerService.checkUsedCoupons(new NetId("example123@test.com"), coupons), res);
    }

    @Test
    void testUpdateAllergens() {
        List<String> allergens = List.of("peanut");
        customerService.updateAllergens(new NetId("example123@test.com"), allergens);
        customer.setAllergens(allergens);
        verify(repo).save(customer);
    }

}
