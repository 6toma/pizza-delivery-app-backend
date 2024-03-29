package nl.tudelft.sem.template.customer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.customer.controllers.CustomerController;
import nl.tudelft.sem.template.customer.domain.Customer;
import nl.tudelft.sem.template.customer.domain.CustomerRepository;
import nl.tudelft.sem.template.customer.domain.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomerControllerTest {

    private transient CustomerController customerController;
    private transient CustomerService customerService;
    private transient AuthManager authManager;
    private transient Customer customer;
    private transient CustomerRepository repo;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(CustomerRepository.class);
        authManager = Mockito.mock(AuthManager.class);
        customerService = new CustomerService(repo);
        customerController = new CustomerController(authManager, customerService);

        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123@test.com");

        customer = new Customer(netId);
        customer.setCustomerId(customerId);
        customer.setAllergens(allergens);
        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        customer.setUsedCoupons(usedCoupons);
    }

    @Test
    public void testGetCustomerById() {
        when(repo.findById(123456)).thenReturn(Optional.of(customer));
        ResponseEntity<Customer> response = customerController.getCustomerById(123456);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(customer);
    }

    @Test
    public void testGetCustomerByNetId() {
        when(repo.findByNetId(new NetId("example123@test.com"))).thenReturn(Optional.of(customer));

        ResponseEntity<Customer> response = customerController.getCustomerByNetId("example123@test.com");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(customer);
    }

    @Test
    public void testGetCustomers() {

        List<String> allergens = Arrays.asList("fish", "stupidity");
        int customerId = 56789;
        NetId netId = new NetId("hello1997@gmail.com");

        List<String> usedCoupons = Arrays.asList("coupon1");
        Customer customer2 = new Customer(netId);
        customer2.setCustomerId(customerId);
        customer2.setAllergens(allergens);
        customer2.setUsedCoupons(usedCoupons);

        List<Customer> res = Arrays.asList(customer, customer2);
        when(repo.findAll()).thenReturn(Arrays.asList(customer, customer2));
        List<Customer> customers = customerController.getCustomers();
        assertThat(customers.containsAll(res));
    }

    @Test
    public void testAddCustomer() {
        String netId = "example123@gmail.com";

        ResponseEntity<String> response = customerController.addCustomer(netId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Mockito.verify(repo).save(new Customer(new NetId(netId)));
    }

    @Test
    public void testAddToUsedCoupons() {
        when(repo.findByNetId(new NetId("example123@test.com"))).thenReturn(Optional.of(customer));
        ResponseEntity<String> response = customerController.addToUsedCoupons("example123@test.com", "coupon3");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertTrue(customer.getUsedCoupons().contains("coupon1"));
        assertTrue(customer.getUsedCoupons().contains("coupon2"));
        assertTrue(customer.getUsedCoupons().contains("coupon3"));
    }
}