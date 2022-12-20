package nl.tudelft.sem.store.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nl.tudelft.sem.template.authentication.UserEmail;


@Entity
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Builder
@EqualsAndHashCode(of = "store_id")
public class Store {

    @Id
    @Column(name = "store_id", nullable = false, unique = true)
    long storeId;

    @Column(name = "store_name", nullable = false, unique = true)
    String storeName;

    @Column(name = "address", nullable = false)
    StoreAddress location;

    @Column(name = "store_owner_email", nullable = false)
    UserEmail storeOwnerEmail;

    /**
     * Dummy method for now to prepare pizza.
     */
    public void preparePizza() {
        System.out.println("Preparing pizza");
    }
}
