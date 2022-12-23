package nl.tudelft.sem.template.authentication.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring boot configuration for integration testing in this module. Since the module doesn't contain a spring boot
 * application, we require this configuration for the integration tests to work.
 */
@SpringBootConfiguration
@ComponentScan("nl.tudelft.sem.template.authentication")
public class TestServerConfig {
}
