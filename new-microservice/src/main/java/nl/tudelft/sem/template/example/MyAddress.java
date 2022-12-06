package nl.tudelft.sem.template.example;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyAddress {
    private String street;
    private int number;
    private String postalCode;
}
