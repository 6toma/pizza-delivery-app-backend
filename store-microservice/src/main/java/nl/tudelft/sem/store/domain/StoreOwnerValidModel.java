package nl.tudelft.sem.store.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreOwnerValidModel {
    private String netId;
    private long storeId;
}
