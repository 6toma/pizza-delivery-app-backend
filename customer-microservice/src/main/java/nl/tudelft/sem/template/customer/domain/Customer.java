package nl.tudelft.sem.template.customer.domain;

import java.util.ArrayList;
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

     * @param netId  - unique netId
     */
    public Customer(NetId netId) {
        this.usedCoupons = new ArrayList<>();
        this.allergens = new ArrayList<>();
        this.netId = netId;
    }

}
