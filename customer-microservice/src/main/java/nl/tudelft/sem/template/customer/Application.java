package nl.tudelft.sem.template.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Example microservice application.
 */
@SpringBootApplication
@ComponentScan({"nl.tudelft.sem.template.authentication", "nl.tudelft.sem.template.customer.controllers",
    "nl.tudelft.sem.template.customer.domain", "nl.tudelft.sem.template.commons"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
