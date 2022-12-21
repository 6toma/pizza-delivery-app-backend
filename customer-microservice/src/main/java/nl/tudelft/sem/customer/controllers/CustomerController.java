package nl.tudelft.sem.customer.controllers;

import lombok.Data;
import nl.tudelft.sem.customer.domain.Customer;
import nl.tudelft.sem.customer.domain.CustomerService;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@Data
@RequestMapping("/customers")
public class CustomerController {

    private final transient AuthManager authManager;
    private final transient CustomerService customerService;

    @Autowired
    public CustomerController(AuthManager authManager, CustomerService customerService) {
        this.authManager = authManager;
        this.customerService = customerService;
    }

    /**
     * Endpoint to retrieve a Customer from the Customer Repository by their unique id.
     *
     * @param customerId the id to search for.
     * @return the Customer with the specified id.
     */
    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable int customerId) {
        return customerService.getCustomerById(customerId);
    }

    /**
     * Endpoint to retrieve a Customer from the Customer Repository by their (unique) NetId.
     *
     * @param netId the netId to search for.
     * @return the Customer with the specified netId.
     */
    @GetMapping("/{netId}")
    public Customer getCustomerByNetId(@PathVariable String netId) {
        NetId customerNetId = new NetId(netId);
        return customerService.getCustomerByNetId(customerNetId);
    }

    /**
     * Endpoint that gets all the customers from the Customer Repository.
     *
     * @return the List of all Customers.
     */
    @GetMapping("/getAll")
    public List<Customer> getCustomers() {
        return customerService.getAll();
    }

    /**
     * Endpoint to save a Customer to the Customer Repository.
     *
     * @param customer the Customer object to save to the repo.
     * @return ok
     */
    @PostMapping
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer) {
        customerService.addCustomer(customer);
        return ResponseEntity.ok("Customer added.");
    }

    /**
     * Endpoint to add a coupon Code to the customer's list of used coupons.
     * > Should be called after an order with a coupon is placed by a customer.
     *
     * @param couponCode the coupon code used by the customer,
     *                   i.e. the coupon to be added to the list of used coupons
     * @return ok
     */
    @PostMapping("/{customerId}/coupons")
    public ResponseEntity<String> addToUsedCoupons(@PathVariable String netId, @RequestBody String couponCode) {

        // make netID object using netID path variable
        NetId customerNetId = new NetId(netId);
        // use that to find customerId

        customerService.addToUsedCoupons(customerNetId, couponCode);
        return ResponseEntity.ok("Coupon used.");
    }

    //TODO: make endpoint to remove from used coupons

    /**
     * Endpoint to set a customer's list of allergens.
     * The endpoint adds the given list of allergens to the existing list of allergens of the customer with the provided id.
     *
     * @param newToppings the new list of toppings to be added to the existing List of Allergens.
     * @return ok
     */
    @PostMapping("/allergens")
    public ResponseEntity<String> updateAllergens(@RequestBody List<String> newToppings) {
        // get netid of customer IN CURRENT CONTEXT
        NetId netId = new NetId(authManager.getNetId());
        // then use that to find the customer in the DB to update allergens
        customerService.updateAllergens(netId, newToppings);
        return ResponseEntity.ok("Allergens updated.");
    }
}
