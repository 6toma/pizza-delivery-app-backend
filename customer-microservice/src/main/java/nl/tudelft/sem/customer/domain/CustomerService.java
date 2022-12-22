package nl.tudelft.sem.customer.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.UserEmail;
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
    public Optional<Customer> getCustomerById(int customerId) {
        try {
            return customerRepository.findByCustomerId(customerId);
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException(customerId);
        }
    }

    /**
     * Retrieves a Customer from the Customer Repository by their (unique) UserEmail.
     *
     * @param email the UserEmail to search for.
     * @return the Customer with the specified UserEmail.
     */
    public Customer getCustomerByUserEmail(UserEmail email) {
        try {
            return customerRepository.findByEmail(email);
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException(email);
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
    public void addToUsedCoupons(UserEmail email, String couponCode) {

        Customer customer = getCustomerByUserEmail(email);
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
    public void removeFromUsedCoupons(UserEmail email, String couponCode) {

        Customer customer = getCustomerByUserEmail(email);
        if(customer == null) { return; }

        List<String> coupons = customer.getUsedCoupons();
        coupons.remove(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }

    /**
     * Checks if a specific coupon code has been used by a given customer.
     *
     * @param email String email of the customer for which to perform this check.
     * @param couponCode the coupon code to verify
     * @return true if the coupon has been used, false otherwise
     */
    public boolean hasUsedCoupon(UserEmail email, String couponCode) {
        Customer customer = getCustomerByUserEmail(email);
        return customer.getUsedCoupons().contains(couponCode);
    }

    /**
     * Checks which coupons have not yet been used out of a list of coupons.
     *
     * @param couponCodes the list of coupon codes to evaluate
     * @return a list of all the coupons that have not been used yet out of that list
     */
    public List<String> checkUsedCoupons(UserEmail email, List<String> couponCodes) {
        Customer customer = getCustomerByUserEmail(email);
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
     * @param email the id of the Customer whose allergens should be updated.
     * @param newToppings the new list of toppings to be added to the existing List of Allergens.
     */
    public void updateAllergens(UserEmail email, List<String> newToppings) {
        Customer customer = customerRepository.findByEmail(email);
        if(customer == null) { return; }

        List<String> toppings = customer.getAllergens();
        toppings.addAll(newToppings);
        customer.setAllergens(toppings);

        customerRepository.save(customer);
    }
}
