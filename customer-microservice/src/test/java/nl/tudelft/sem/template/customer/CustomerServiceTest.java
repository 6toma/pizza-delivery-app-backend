package nl.tudelft.sem.template.customer;

import nl.tudelft.sem.template.customer.domain.Customer;
import nl.tudelft.sem.template.customer.domain.CustomerNotFoundException;
import nl.tudelft.sem.template.customer.domain.CustomerRepository;
import nl.tudelft.sem.template.customer.domain.CustomerService;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    private CustomerService customerService;
    private AuthManager authManager;
    private Customer customer;
    private CustomerRepository repo;

    @BeforeEach
    public void setup() {
        repo = Mockito.mock(CustomerRepository.class);
        authManager = Mockito.mock(AuthManager.class);


        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123");

        customer = new Customer(netId);
            customer.setCustomerId(customerId);
            customer.setAllergens(allergens);
            customer.setUsedCoupons(usedCoupons);

        when(repo.findByNetId(new NetId("example123"))).thenReturn(java.util.Optional.ofNullable(customer));
        customerService = new CustomerService(repo);
    }

    @Test
    void TestGetCustomerByIdSuccess() {
        when(repo.findById(123456)).thenReturn(java.util.Optional.ofNullable(customer));
        assertEquals(customerService.getCustomerById(123456), customer);
    }

    @Test
    void TestGetCustomerByIdException() {
        when(repo.findById(123456)).thenReturn(java.util.Optional.ofNullable(null));
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(123456);
        });
    }

    @Test
    void TestGetCustomerByNetIdSuccess() {
        assertEquals(customerService.getCustomerByNetId(new NetId("example123")), customer);
    }

    @Test
    void TestGetCustomerByNetIdException() {
        when(repo.findByNetId(new NetId("example123"))).thenReturn(java.util.Optional.ofNullable(null));
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerByNetId(new NetId("example123"));
        });
    }

    @Test
    void TestGetAll() {
        when(repo.findAll()).thenReturn(List.of(customer));
        assertEquals(customerService.getAll(), List.of(customer));
    }

    @Test
    void TestAddCustomer() {
        when(repo.save(customer)).thenReturn(customer);
        assertEquals(customerService.addCustomer(customer), customer);
    }

    @Test
    void TestAddToUsedCouponsSuccess() {
        String coupon = "coupon3";
        customerService.addToUsedCoupons(customer.getNetId(), coupon);
        List<String> coupons = new ArrayList<>(customer.getUsedCoupons());
        coupons.add(coupon);
        customer.setUsedCoupons(coupons);
        verify(repo).save(customer);
    }

    @Test
    void TestRemoveFromUsedCouponsSuccess() {
        String coupon = "coupon2";
        customerService.removeFromUsedCoupons(customer.getNetId(), coupon);
        List<String> coupons = Arrays.asList("coupon1");
        customer.setUsedCoupons(coupons);
        verify(repo).save(customer);
    }

    @Test
    void TestHasUsedCouponsTrue() {
        String coupon = "coupon2";
        assertTrue(customerService.hasUsedCoupon(new NetId("example123"), coupon));
    }

    @Test
    void TestHasUsedCouponsFalse() {
        String coupon = "coupon3";
        assertFalse(customerService.hasUsedCoupon(new NetId("example123"), coupon));
    }

    @Test
    void TestCheckUsedCoupons() {
        List<String> res = Arrays.asList("coupon3");
        List<String> coupons = Arrays.asList("coupon1", "coupon3");
        assertEquals(customerService.checkUsedCoupons(new NetId("example123"), coupons), res);
    }

    @Test
    void TestUpdateAllergens() {
        List<String> allergens = Arrays.asList("peanut");
        customerService.updateAllergens(new NetId("example123"), allergens);
        customer.setAllergens(allergens);
        verify(repo).save(customer);
    }

}
