package nl.tudelft.sem.template.store.domain;

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
import nl.tudelft.sem.template.authentication.NetId;


@Entity
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Builder
@EqualsAndHashCode(of = "storeId", callSuper = false)
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

}
