package nl.tudelft.sem.store.domain;

import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable // In order to put the field from Store address directly into the Store table
@NoArgsConstructor
@AllArgsConstructor
public class StoreAddress {
    private String zipCode;
    private String street;
    private int number;
}
