package nl.tudelft.sem.store.domain;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StoreOwnerValidModel that = (StoreOwnerValidModel) o;
        return storeId == that.storeId && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, storeId);
    }
}
