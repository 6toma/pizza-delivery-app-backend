package nl.tudelft.sem.template.example;


import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
public class Company {

    private final transient MyAddress address;
    private transient Manager manager;

}
