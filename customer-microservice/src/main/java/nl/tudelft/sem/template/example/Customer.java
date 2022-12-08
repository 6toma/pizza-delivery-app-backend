package nl.tudelft.sem.template.example;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    List<String> usedCoupons;
    List<String> allergens;
    String customerId;

    /** Constructor for Customer object.
     *
     * @param usedCoupons - the list of coupons that have been used by this customer.
     * @param allergens - the list of allergens that this customer has set for themselves.
     * @param customerId - unique customer ID //should match user ID in some way 
     */
    public Customer(List<String> usedCoupons, List<String> allergens, String customerId) {
        this.usedCoupons = usedCoupons;
        this.allergens = allergens;
        this.customerId = customerId;
    }

}
