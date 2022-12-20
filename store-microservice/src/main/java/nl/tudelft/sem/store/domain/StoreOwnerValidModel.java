package nl.tudelft.sem.store.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO Object used to check if a store owner is valid.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreOwnerValidModel {
    private String email;
    private long storeId;
}
