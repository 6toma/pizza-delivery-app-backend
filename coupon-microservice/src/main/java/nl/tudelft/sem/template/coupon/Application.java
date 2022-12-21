package nl.tudelft.sem.template.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Coupon microservice application.
 */
@SpringBootApplication
@ComponentScan({"nl.tudelft.sem.template.commons.utils", "nl.tudelft.sem.template.coupon"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
