package nl.tudelft.sem.customer.controllers;

import nl.tudelft.sem.customer.domain.Customer;
import nl.tudelft.sem.customer.domain.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerRepository customerRepository;

    /**
     * Endpoint to get a Customer with a specific id from the Customer Repository.
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
     * @return
     */
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Endpoint to add a coupon Code to the customer's list of used coupons.
     * > Should be called after an order with a coupon is placed by a customer.
     *
     * @param couponCode the coupon code used by the customer,
     *                   ie. the coupon to be added to the list of used coupons
     */
    @PostMapping("/{id}/coupons")
    public void addToUsedCoupons(@PathVariable("id") String customerId, @RequestBody String couponCode) {
        Customer customer = getCustomer(customerId);
        if(customer == null) { return; }

        List<String> coupons = customer.getUsedCoupons();
        coupons.add(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }

    /**
     * Endpoint to set a customer's list of allergens.
     * The endpoint adds the given list of allergens to the list of allergens of the customer with the provided id.
     * @param customerId
     * @param newToppings
     */
    @PostMapping("/{id}/allergens")
    public void updateAllergens(@PathVariable("id") String customerId, @RequestBody List<String> newToppings) {
        Customer customer = getCustomer(customerId);
        if(customer == null) { return; }

        List<String> toppings = customer.getAllergens();
        toppings.addAll(newToppings);
        customer.setAllergens(toppings);

        customerRepository.save(customer);
    }
}
