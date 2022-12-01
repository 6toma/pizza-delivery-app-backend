package nl.tudelft.sem.template.example;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Manager {
    private final String name;
}
