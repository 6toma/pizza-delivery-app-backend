package nl.tudelft.sem.template.coupon.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    @Query(value = "SELECT s FROM Coupon s WHERE s.storeId = :storeId OR s.storeId = -1")
    List<Coupon> findByStoreId(@Param("storeId") long storeId);

}
