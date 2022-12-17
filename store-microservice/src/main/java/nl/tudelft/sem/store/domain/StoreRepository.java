package nl.tudelft.sem.store.domain;

import java.util.List;
import nl.tudelft.sem.template.authentication.NetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query(value = "SELECT store_name FROM store", nativeQuery = true)
    List<String> getAllStoreNames();

    boolean existsByStoreName(String storeName);

    @Query(value = "SELECT store_id FROM store WHERE store_name = :storeName", nativeQuery = true)
    long getStoreIdFromStoreName(String storeName);


//    boolean existsByStoreIdAndStoreOwnerNetId(long storeId, String storeOwnerId);

    //    @Query(
    //        value = "select case when (count(store_id) > 0)  then true else false end FROM store WHERE net_id_value = ?2 AND store_id = ?1",
    //        nativeQuery = true)
    boolean existsByStoreIdAndStoreOwnerNetId(long storeId, NetId storeOwnerId);
}
