package nl.tudelft.sem.template.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Config components.
 */
@Configuration
@PropertySource("classpath:my-application-dev.properties")
public class ConfigComponents {
    private final transient Environment environment;

    /**
     * Creates new config components.
     *
     * @param environment The environment
     */
    @Autowired
    public ConfigComponents(Environment environment) {
        this.environment = environment;
    }

    @Bean
    MyAddress getAddress() {
        return new MyAddress("Some Street", 12, "2627LW");
    }

    @Bean
    Company getCompany() {
        return Company.builder()
            .manager(new Manager(environment.getProperty("manager.name")))
            .build();
    }

    @Bean
    Manager getManager() {
        return new Manager(environment.getProperty("manager.name"));
    }

    //    @Bean
    //    public Manager getManager_2() {
    //        return new Manager(environment.getProperty("not_existing_property","Nick"));
    //    }
}
