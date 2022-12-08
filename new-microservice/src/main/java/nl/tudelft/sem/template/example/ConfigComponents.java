package nl.tudelft.sem.template.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:my-application-dev.properties")
public class ConfigComponents {
    private final Environment environment;

    @Autowired
    public ConfigComponents(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public MyAddress getAddress() {
        return new MyAddress("Some Street", 12, "2627LW");
    }

    @Bean
    public Company getCompany() {
        return Company.builder()
                .manager(new Manager(environment.getProperty("manager.name")))
                .build();
    }

    @Bean
    public Manager getManager() {
        return new Manager(environment.getProperty("manager.name"));
    }

}
