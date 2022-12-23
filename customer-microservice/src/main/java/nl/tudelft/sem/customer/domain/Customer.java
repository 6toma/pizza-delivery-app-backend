package nl.tudelft.sem.customer.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ElementCollection;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;

import lombok.Data;

@Entity
@Data
@NoArgsConstructor
@Table(name = "customer")
@NoArgsConstructor
public class Customer {

    @Id
    @Column(name = "customerId", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int customerId;

    @Column(name = "netId", unique = true, nullable = false)
    private NetId netId;

    @Column(name = "usedCoupons")
    @ElementCollection
    private List<String> usedCoupons;

    @Column(name = "allergenList")
    @ElementCollection
    private List<String> allergens;

    /**
     * Constructor for Customer object.
     *
     * @param usedCoupons - the list of coupons that have been used by this customer.
     * @param allergens   - the list of allergens that this customer has set for themselves.
     * @param customerId  - unique customer ID //should match user ID in some way
     */
    public Customer(List<String> usedCoupons, List<String> allergens, int customerId, NetId netId) {
        this.usedCoupons = usedCoupons;
        this.allergens = allergens;
        this.customerId = customerId;
        this.netId = netId;
    }

}
