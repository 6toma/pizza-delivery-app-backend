package nl.tudelft.sem.store.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nl.tudelft.sem.template.authentication.NetId;
import org.hibernate.Hibernate;


@Entity
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Builder
public class Store {

    @Id
    @Column(name = "store_id", nullable = false, unique = true)
    long storeId;

    @Column(name = "store_name", nullable = false, unique = true)
    String storeName;

    @Column(name = "address", nullable = false)
    StoreAddress location;

    @Column(name = "store_owner_netid", nullable = false)
    NetId storeOwnerNetId;

    /**
     * Dummy method for now to prepare pizza.
     */
    public void preparePizza() {
        System.out.println("Preparing pizza");
    }

    // just to make sure the storeId is in equals
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Store store = (Store) o;
        return storeId == store.storeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId);
    }

}
