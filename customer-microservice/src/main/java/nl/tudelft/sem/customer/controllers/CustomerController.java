package nl.tudelft.sem.customer.controllers;

import nl.tudelft.sem.customer.domain.Customer;
import nl.tudelft.sem.customer.domain.CustomerService;
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
@RequestMapping("/customers")
public class CustomerController {

    private final transient CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
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
     * @param netId the neIid to search for.
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
     * @param customerId the id of the Customer whose allergens should be updated.
     * @param newToppings the new list of toppings to be added to the existing List of Allergens.
     * @return ok
     */
    @PostMapping("/{customerId}/allergens")
    public ResponseEntity<String> updateAllergens(@PathVariable int customerId, @RequestBody List<String> newToppings) {
        // use authmanager.getnetId or something to get netid of current customer
        // then use that to find the customer in the DB to update allergens
        customerService.updateAllergens(customerId, newToppings);
        return ResponseEntity.ok("Allergens updated.");
    }
}
