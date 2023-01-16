package nl.tudelft.sem.template.customer.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;
import org.springframework.stereotype.Service;

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
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerId);
        }
        return customer.get();
    }

    /**
     * Retrieves a Customer from the Customer Repository by their (unique) NetId.
     *
     * @param netId the NetId to search for.
     * @return the Customer with the specified NetId.
     */
    public Customer getCustomerByNetId(NetId netId) {
        Optional<Customer> customer = customerRepository.findByNetId(netId);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundException(netId);
        }
        return customer.get();
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
     * @param netId the netId of the Customer to add to the Repository.
     * @return the Customer saved.
     */
    public Customer addCustomer(String netId) {
        var customer = new Customer();
        customer.setNetId(new NetId(netId));
        customer.setAllergens(Collections.emptyList());
        customer.setUsedCoupons(Collections.emptyList());
        return customerRepository.save(customer);
    }

    /**
     * Adds a coupon Code to the Customer's list of used coupons.
     *
     * @param couponCode the coupon code used by the customer, i.e. the coupon to be added to the list of used coupons.
     */
    public void addToUsedCoupons(NetId netId, String couponCode) {

        Customer customer = getCustomerByNetId(netId);

        List<String> coupons = new ArrayList<>(customer.getUsedCoupons());
        coupons.add(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }

    /**
     * Removes a coupon Code from the Customer's list of used coupons.
     *
     * @param couponCode the coupon code that should not anymore be used by the customer, i.e. the coupon to be removed from
     *                   the list of used coupons.
     */
    public void removeFromUsedCoupons(NetId netId, String couponCode) {

        Customer customer = getCustomerByNetId(netId);
        if (customer == null) {
            return;
        }

        List<String> coupons = new ArrayList<>(customer.getUsedCoupons());
        coupons.remove(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }

    /**
     * Checks if a specific coupon code has been used by a given customer.
     *
     * @param netId      String netId of the customer for which to perform this check.
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
        if (usedCoupons == null || usedCoupons.isEmpty()) {
            return couponCodes;
        }
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
     * @param netId       the id of the Customer whose allergens should be updated.
     * @param newToppings the new list of toppings to be added to the existing List of Allergens.
     */
    public void updateAllergens(NetId netId, List<String> newToppings) {
        Customer customer = getCustomerByNetId(netId);

        customer.setAllergens(newToppings);

        customerRepository.save(customer);
    }
}
