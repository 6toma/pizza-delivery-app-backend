package nl.tudelft.sem.customer.controllers;

import nl.tudelft.sem.customer.domain.Customer;
import nl.tudelft.sem.customer.domain.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
     * Endpoint to get a Customer with a specific id from the Customer Repository.
     * @param customerId = the id to search for
     * @return the customer with the specified id
     */
    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable int customerId) {
        return customerService.getCustomer(customerId);
    }

    /**
     * Endpoint to save a Customer to the Customer Repository.
     *
     * @param customer = the Customer object to save to the repo
     * @return ok
     */
    @PostMapping
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer) {
        customerService.addCustomer(customer);
        return ResponseEntity.ok("Customer added");
    }

    /**
     * Endpoint to add a coupon Code to the customer's list of used coupons.
     * > Should be called after an order with a coupon is placed by a customer.
     *
     * @param couponCode the coupon code used by the customer,
     *                   ie. the coupon to be added to the list of used coupons
     * @return ok
     */
    @PostMapping("/{id}/coupons")
    public ResponseEntity<String> addToUsedCoupons(@PathVariable int customerId, @RequestBody String couponCode) {
        customerService.addToUsedCoupons(customerId, couponCode);
        return ResponseEntity.ok("Coupon used");
    }

    /**
     * Endpoint to set a customer's list of allergens.
     * The endpoint adds the given list of allergens to the list of allergens of the customer with the provided id.
     * @param customerId
     * @param newToppings
     */
    @PostMapping("/{id}/allergens")
    public ResponseEntity<String> updateAllergens(@PathVariable int customerId, @RequestBody List<String> newToppings) {
        customerService.updateAllergens(customerId, newToppings);
        return ResponseEntity.ok("Allergens updated");
    }
}
