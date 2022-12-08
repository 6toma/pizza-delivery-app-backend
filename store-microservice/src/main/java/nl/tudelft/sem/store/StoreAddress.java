package nl.tudelft.sem.store;

import lombok.*;

import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class StoreAddress {
    private String zipCode;
    private String street;
    private int number;

}
