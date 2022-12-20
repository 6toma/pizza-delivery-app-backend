package nl.tudelft.sem.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EntityScan({"nl.tudelft.sem.template.commons.entity", "nl.tudelft.sem.store"})
@ComponentScan({"nl.tudelft.sem.template.authentication", "nl.tudelft.sem.store",
    "nl.tudelft.sem.template.commons"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
