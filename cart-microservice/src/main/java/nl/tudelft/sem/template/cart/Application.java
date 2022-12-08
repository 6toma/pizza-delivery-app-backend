package nl.tudelft.sem.template.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EntityScan("nl.tudelft.sem.template.commons.entity")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
