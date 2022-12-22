package nl.tudelft.sem.customer.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.authentication.NetId;

/**
 * Customer Service responsible for managing the Customer Repository.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final transient CustomerRepository customerRepository;

    /**
     * Retrieves a Customer from the Customer Repository by their unique id.
     *
     * @param customerId the id to search for.
     * @return the Customer with the specified id.
     */
    public Customer getCustomerById(int customerId) {
        try {
            return customerRepository.findById(customerId);
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException(customerId);
        }
    }

    /**
     * Retrieves a Customer from the Customer Repository by their (unique) NetId.
     *
     * @param netId the NetId to search for.
     * @return the Customer with the specified NetId.
     */
    public Customer getCustomerByNetId(NetId netId) {
        try {
            return customerRepository.findByNetId(netId);
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException(netId);
        }
    }


    /**
     * Retrieves all the customers from the Customer Repository.
     *
     * @return the List of all Customers.
     */
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    /**
     * Adds (and returns) a customer to the Customer Repository.
     *
     * @param customer the Customer to add to the Repository.
     * @return the Customer
     */
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Adds a coupon Code to the Customer's list of used coupons.
     *
     * @param couponCode the coupon code used by the customer,
     *                   i.e. the coupon to be added to the list of used coupons.
     */
    public void addToUsedCoupons(NetId netId, String couponCode) {

        Customer customer = getCustomerByNetId(netId);
        if(customer == null) { return; }

        List<String> coupons = customer.getUsedCoupons();
        coupons.add(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }
    
    /**
     * Removes a coupon Code from the Customer's list of used coupons.
     *
     * @param couponCode the coupon code that should not anymore be used by the customer,
     *                   i.e. the coupon to be removed from the list of used coupons.
     */
    public void removeFromUsedCoupons(NetId netId, String couponCode) {

        Customer customer = getCustomerByNetId(netId);
        if(customer == null) { return; }

        List<String> coupons = customer.getUsedCoupons();
        coupons.remove(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }

    /**
     * Checks if a specific coupon code has been used by a given customer.
     *
     * @param netId String netId of the customer for which to perform this check.
     * @param couponCode the coupon code to verify
     * @return true if the coupon has been used, false otherwise
     */
    public boolean hasUsedCoupon(NetId netId, String couponCode) {
        Customer customer = getCustomerByNetId(netId);
        return customer.getUsedCoupons().contains(couponCode);
    }

    /**
     * Checks which coupons have not yet been used out of a list of coupons.
     *
     * @param couponCodes the list of coupon codes to evaluate
     * @return a list of all the coupons that have not been used yet out of that list
     */
    public List<String> checkUsedCoupons(NetId netId, List<String> couponCodes) {
        Customer customer = getCustomerByNetId(netId);
        List<String> usedCoupons = customer.getUsedCoupons();
        List<String> unusedCoupons = new ArrayList<>();
        for (String couponCode : couponCodes) {
            if (!usedCoupons.contains(couponCode)) {
                unusedCoupons.add(couponCode);
            }
        }
        return unusedCoupons;
    }


    /**
     * Adds the given list of allergens to the existing list of allergens of the Customer with the provided id.
     *
     * @param netId the id of the Customer whose allergens should be updated.
     * @param newToppings the new list of toppings to be added to the existing List of Allergens.
     */
    public void updateAllergens(NetId netId, List<String> newToppings) {
        Customer customer = customerRepository.findByNetId(netId);
        if(customer == null) { return; }

        List<String> toppings = customer.getAllergens();
        toppings.addAll(newToppings);
        customer.setAllergens(toppings);

        customerRepository.save(customer);
    }
}