package nl.tudelft.sem.store.domain;

import java.util.List;
import nl.tudelft.sem.template.authentication.NetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query(value = "SELECT store_name FROM store", nativeQuery = true)
    List<String> getAllStoreNames();

    boolean existsByStoreName(String storeName);

    @Query(value = "SELECT store_id FROM store WHERE store_name = :storeName"
        , nativeQuery = true)
    Long getStoreIdFromStoreName(String storeName);

    boolean existsByStoreIdAndStoreOwnerNetId(long storeId, NetId storeOwnerId);
}
