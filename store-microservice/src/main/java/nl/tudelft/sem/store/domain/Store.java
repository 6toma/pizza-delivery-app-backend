package nl.tudelft.sem.store.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    @Getter
    long storeId;


    @Column(name = "address", nullable = false)
    StoreAddress location;

    /**
     * Dummy method for now to prepare pizza.
     */
    public void preparePizza() {
        System.out.println("Preparing pizza");
    }

}
