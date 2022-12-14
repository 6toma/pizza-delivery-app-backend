package nl.tudelft.sem.template.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Example microservice application.
 */
@SpringBootApplication
@ComponentScan({"nl.tudelft.sem.template.authentication", "nl.tudelft.sem.template.example"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
