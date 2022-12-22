package nl.tudelft.sem.customer.controllers;

import java.util.List;
import nl.tudelft.sem.customer.Customer;
import nl.tudelft.sem.customer.CustomerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for customers.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private transient CustomerRepository customerRepository;

    /**
     * Endpoint to get a Customer with a specific id from the Customer Repository.
     *
     * @param customerId = the id to search for
     * @return the customer with the specified id
     */
    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable("id") String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    /**
     * Endpoint to save a Customer to the Customer Repository.
     *
     * @param customer = the Customer object to save to the repo
     * @return The newly created customer
     */
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Endpoint to add a coupon Code to the customer's list of used coupons. > Should be called after an order with a coupon
     * is placed by a customer.
     *
     * @param couponCode the coupon code used by the customer, ie. the coupon to be added to the list of used coupons
     */
    @PostMapping("/{id}/coupons")
    public void addToUsedCoupons(@PathVariable("id") String customerId, @RequestBody String couponCode) {
        Customer customer = getCustomer(customerId);
        if (customer == null) {
            return;
        }

        List<String> coupons = customer.getUsedCoupons();
        coupons.add(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }

    /**
     * Endpoint to set a customer's list of allergens. The endpoint adds the given list of allergens to the list of allergens
     * of the customer with the provided id.
     *
     * @param customerId  The id of the customer
     * @param newToppings The topic they're allergic to
     */
    @PostMapping("/{id}/allergens")
    public void updateAllergens(@PathVariable("id") String customerId, @RequestBody List<String> newToppings) {
        Customer customer = getCustomer(customerId);
        if (customer == null) {
            return;
        }

        List<String> toppings = customer.getAllergens();
        toppings.addAll(newToppings);
        customer.setAllergens(toppings);

        customerRepository.save(customer);
    }
}
