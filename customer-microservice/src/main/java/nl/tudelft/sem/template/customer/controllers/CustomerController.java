package nl.tudelft.sem.template.customer.controllers;

import java.util.Collections;
import javax.validation.constraints.NotNull;
import lombok.Data;
import nl.tudelft.sem.template.customer.domain.Customer;
import nl.tudelft.sem.template.customer.domain.CustomerService;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.annotations.role.MicroServiceInteraction;
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
    @GetMapping("/id/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable int customerId){
        Customer customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    /**
     * Endpoint to retrieve a Customer from the Customer Repository by their (unique) NetId.
     *
     * @param netId the netId to search for.
     * @return the Customer with the specified netId.
     */
    @GetMapping("/netId/{netId}")
    public ResponseEntity<Customer> getCustomerByNetId(@PathVariable String netId) {
        NetId customerNetId = new NetId(netId);
        Customer customer = customerService.getCustomerByNetId(customerNetId);
        return ResponseEntity.ok(customer);
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
     * @param netId the net id to save to the repo.
     * @return ok
     */
    @MicroServiceInteraction
    @PostMapping("/add")
    public ResponseEntity<String> addCustomer(@NotNull @RequestBody String netId) {

        var customer = new Customer();
        customer.setNetId(new NetId(netId));
        customer.setAllergens(Collections.emptyList());
        customer.setUsedCoupons(Collections.emptyList());

        customerService.addCustomer(customer);
        return ResponseEntity.ok("Customer added.");
    }

    /**
     * Endpoint to add a coupon Code to the customer's list of used coupons. > Should be called after an order with a coupon
     * is placed by a customer.
     *
     * @param couponCode the coupon code used by the customer, i.e. the coupon to be added to the list of used coupons
     * @return ok
     */
    @PostMapping("/{netId}/coupons/add")
    public ResponseEntity<String> addToUsedCoupons(@PathVariable String netId, @RequestBody String couponCode) {

        // make netID object using netID path variable
        NetId customerNetId = new NetId(netId);
        // use that to find customerId

        customerService.addToUsedCoupons(customerNetId, couponCode);
        return ResponseEntity.ok("Coupon used.");
    }

    /**
     * Endpoint to remove a coupon Code to the customer's list of used coupons. > Should be called after an order with a
     * coupon is cancelled.
     *
     * @param couponCode the coupon code used by the customer, i.e. the coupon to be added to the list of used coupons
     * @return ok
     */
    @MicroServiceInteraction
    @PostMapping("/{netId}/coupons/remove")
    public ResponseEntity<String> removeFromUsedCoupons(@PathVariable String netId, @RequestBody String couponCode) {

        NetId customerNetId = new NetId(netId);

        customerService.removeFromUsedCoupons(customerNetId, couponCode);
        return ResponseEntity.ok("Coupon unused.");
    }


    /**
     * Endpoint for checking if a specific coupon code has been used by a customer.
     *
     * @param netId      String netId of the customer for which to perform this check.
     * @param couponCode the coupon code to verify
     * @return true if the coupon has been used, false otherwise
     */
    @GetMapping("/{netId}/coupons/{couponCode}")
    public ResponseEntity<Boolean> hasUsedCoupon(@PathVariable String netId, @PathVariable String couponCode) {
        NetId customerNetId = new NetId(netId);
        boolean hasUsedCoupon = customerService.hasUsedCoupon(customerNetId, couponCode);
        return ResponseEntity.ok(hasUsedCoupon);
    }

    /**
     * Endpoint that takes a list of String coupon codes and returns which coupons have not yet been used
     *
     * @param couponCodes the list of coupon codes to evaluate
     * @return a list of all the coupons that have not been used yet out of that list
     */
    @PostMapping("/checkUsedCoupons/{netId}")
    @MicroServiceInteraction
    public ResponseEntity<List<String>> checkUsedCoupons(@PathVariable String netId, @RequestBody List<String> couponCodes) {
        NetId customerNetId = new NetId(netId);
        return ResponseEntity.ok(customerService.checkUsedCoupons(customerNetId, couponCodes));
    }

    /**
     * Endpoint to set a customer's list of allergens. The endpoint adds the given list of allergens to the existing list of
     * allergens of the customer with the provided id.
     *
     * @param newToppings the new list of toppings to be added to the existing List of Allergens.
     * @return ok
     */
    @PostMapping("/allergens")
    public ResponseEntity<String> updateAllergens(@RequestBody List<String> newToppings) {
        // get netid of customer IN CURRENT CONTEXT
        NetId netId = new NetId(authManager.getNetId());

        customerService.updateAllergens(netId, newToppings);
        return ResponseEntity.ok("Allergens updated.");
    }

    /**
     * Endpoint to retrieve the allergens of a specific user.
     *
     * @return The user's allergens
     */
    @GetMapping("/allergens/{netId}")
    public ResponseEntity<List<String>> getAllergens(@PathVariable("netId") String netId) {
        Customer customer = customerService.getCustomerByNetId(new NetId(netId));
        if (customer == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(customer.getAllergens());
    }

}
