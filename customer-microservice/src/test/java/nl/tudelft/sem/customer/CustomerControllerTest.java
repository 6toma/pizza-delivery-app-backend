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

    @Test
    public void getCustomerByIdSuccessfully() throws Exception
    {
        int id = 123456;
        Customer cus = customer;
        cus.setCustomerId(id);
        when(repo.existsById(id)).thenReturn(true);
        when(repo.findById(id)).thenReturn(cus);
        Customer res = customerController.getCustomerById(id);
        assertThat(res).isEqualTo(customer);

    }

//    @Test
//    public void getCustomerByIdNotFound() throws Exception
//    {
//        int id = 123456;
//        Customer cus = customer;
//        cus.setCustomerId(id);
//        when(repo.existsById(id)).thenReturn(true);
//        when(repo.findById(id)).thenReturn(cus);
//        Customer res = customerController.getCustomerById(id);
//        assertThat(res).isEqualTo(customer);
//    }



}