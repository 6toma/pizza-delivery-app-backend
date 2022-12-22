package nl.tudelft.sem.customer.controllers;

import java.util.List;
import lombok.Data;
import nl.tudelft.sem.customer.domain.Customer;
import nl.tudelft.sem.customer.domain.CustomerService;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.UserEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return customerService.getCustomerById(customerId).orElse(null);
    }

    /**
     * Endpoint to retrieve a Customer from the Customer Repository by their (unique) UserEmail.
     *
     * @param email the email to search for.
     * @return the Customer with the specified email.
     */
    @GetMapping("/{email}")
    public Customer getCustomerByUserEmail(@PathVariable String email) {
        UserEmail customerUserEmail = new UserEmail(email);
        return customerService.getCustomerByUserEmail(customerUserEmail);
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
     * Endpoint to add a coupon Code to the customer's list of used coupons. > Should be called after an order with a coupon
     * is placed by a customer.
     *
     * @param couponCode the coupon code used by the customer, i.e. the coupon to be added to the list of used coupons
     * @return ok
     */
    @PostMapping("/{email}/coupons/add")
    public ResponseEntity<String> addToUsedCoupons(@PathVariable String email, @RequestBody String couponCode) {

        // make email object using email path variable
        UserEmail customerUserEmail = new UserEmail(email);
        // use that to find customerId

        customerService.addToUsedCoupons(customerUserEmail, couponCode);
        return ResponseEntity.ok("Coupon used.");
    }

    /**
     * Endpoint to remove a coupon Code to the customer's list of used coupons. > Should be called after an order with a
     * coupon is cancelled.
     *
     * @param couponCode the coupon code used by the customer, i.e. the coupon to be added to the list of used coupons
     * @return ok
     */
    @PostMapping("/{email}/coupons/remove")
    public ResponseEntity<String> removeFromUsedCoupons(@PathVariable String email, @RequestBody String couponCode) {

        UserEmail customerUserEmail = new UserEmail(email);

        customerService.removeFromUsedCoupons(customerUserEmail, couponCode);
        return ResponseEntity.ok("Coupon unused.");
    }


    /**
     * Endpoint for checking if a specific coupon code has been used by a customer.
     *
     * @param email      String email of the customer for which to perform this check.
     * @param couponCode the coupon code to verify
     * @return true if the coupon has been used, false otherwise
     */
    @GetMapping("/{email}/coupons/{couponCode}")
    public ResponseEntity<Boolean> hasUsedCoupon(@PathVariable String email, @PathVariable String couponCode) {
        UserEmail customerUserEmail = new UserEmail(email);
        boolean hasUsedCoupon = customerService.hasUsedCoupon(customerUserEmail, couponCode);
        return ResponseEntity.ok(hasUsedCoupon);
    }

    /**
     * Endpoint that takes a list of String coupon codes and returns which coupons have not yet been used
     *
     * @param couponCodes the list of coupon codes to evaluate
     * @return a list of all the coupons that have not been used yet out of that list
     */
    @GetMapping("/checkUsedCoupons")
    public List<String> checkUsedCoupons(@RequestParam("couponCodes") List<String> couponCodes) {
        UserEmail email = new UserEmail(authManager.getEmail());
        return customerService.checkUsedCoupons(email, couponCodes);
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
        // get email of customer IN CURRENT CONTEXT
        UserEmail email = new UserEmail(authManager.getEmail());
        // then use that to find the customer in the DB to update allergens
        customerService.updateAllergens(email, newToppings);
        return ResponseEntity.ok("Allergens updated.");
    }

}
