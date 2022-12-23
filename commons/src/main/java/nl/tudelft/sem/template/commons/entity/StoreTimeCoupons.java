package nl.tudelft.sem.template.commons.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class StoreTimeCoupons {
    private final String storeName;
    private final LocalDateTime pickupTime;
    private final List<String> coupons;
}
