package nl.tudelft.sem.template.commons.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO Object used to check if a store owner is valid.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StoreOwnerValidModel {
    private String netId;
    private long storeId;
}
