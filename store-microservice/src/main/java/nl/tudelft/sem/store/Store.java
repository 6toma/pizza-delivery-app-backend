package nl.tudelft.sem.store;

import lombok.*;

import javax.persistence.*;

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
    long storeId;


    @Column(name = "address", nullable = false)
    StoreAddress location;


    public void preparePizza() {
        System.out.println("Preparing pizza");
    }

}
