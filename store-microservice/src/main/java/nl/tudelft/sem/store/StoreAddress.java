package nl.tudelft.sem.store;

import lombok.*;

import javax.persistence.Embeddable;

@Data
@Embeddable // In order to put the field from Store address directly into the Store table
@NoArgsConstructor
@AllArgsConstructor
public class StoreAddress {
    private String zipCode;
    private String street;
    private int number;

}
