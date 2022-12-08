package nl.tudelft.sem.template.example;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Customer {
    @Column
    @ElementCollection
    List<String> usedCoupons;
    @Column
    @ElementCollection
    List<String> allergens;
    @Id
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

    public List<String> getUsedCoupons() {
        return usedCoupons;
    }

    public void setUsedCoupons(List<String> usedCoupons) {
        this.usedCoupons = usedCoupons;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }
}
