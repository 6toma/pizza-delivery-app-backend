package nl.tudelft.sem.customer;

import nl.tudelft.sem.customer.controllers.CustomerController;
import nl.tudelft.sem.customer.domain.Customer;
import nl.tudelft.sem.customer.domain.CustomerRepository;
import nl.tudelft.sem.customer.domain.CustomerService;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class CustomerControllerTest {

    private CustomerController customerController;
    private CustomerService customerService;
    private AuthManager authManager;
    private Customer customer;
    private CustomerRepository repo;

    @BeforeEach
    public void setup(){
        repo = Mockito.mock(CustomerRepository.class);
        authManager = Mockito.mock(AuthManager.class);
        customerService = new CustomerService(repo);
        customerController = new CustomerController(authManager, customerService);

        List<String> usedCoupons = Arrays.asList("coupon1", "coupon2");
        List<String> allergens = Arrays.asList("peanut", "gluten");
        int customerId = 123456;
        NetId netId = new NetId("example123");

        customer = new Customer(usedCoupons, allergens, customerId, netId);
    }
//
//    @Test
//    public void getCustomerByNetIdSuccessfully() throws Exception
//    {
//        String netId = "example123";
//        Customer cus = customer;
//        cus.setNetId(new NetId(netId));
//        when(repo.existsByNetId(new NetId(netId))).thenReturn(true);
//        when(repo.findByNetId(new NetId(netId))).thenReturn(cus);
//        ResponseEntity<Customer> res = customerController.getCustomerByNetId(netId);
//        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(res.getBody()).isEqualTo(customer);
//    }
//
//    @Test
//    public void getCustomerByNetIdNotFound() throws Exception
//    {
//        String netId = "example123";
//        when(repo.existsByNetId(new NetId(netId))).thenReturn(false);
//        ResponseEntity<Customer> res = customerController.getCustomerByNetId(netId);
//        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }


}