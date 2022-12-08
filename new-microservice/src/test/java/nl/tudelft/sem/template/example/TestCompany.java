package nl.tudelft.sem.template.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestCompany {
    @Test
    void test() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigComponents.class);
        Company company = Company.builder()
                .address(context.getBean(MyAddress.class))
                .build();
        assertEquals(company.getAddress(), new MyAddress("Some Street", 12, "2627LW"));
    }

    @Test
    void test_2() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigComponents.class);
        Company company = Company.builder()
                .address(context.getBean(MyAddress.class))
                .manager(context.getBean(Manager.class))
                .build();
        assertEquals(company.getManager(), new Manager("Nick"));
    }
}
