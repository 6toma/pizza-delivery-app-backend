package nl.tudelft.sem.testing.profiles;

import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Profile that replaces the original request helper with a mocked request helper that can be used for testing.
 */
@Profile("mockRequestHelper")
@Configuration
public class MockRequestHelperProfile {
    @Bean
    @Primary
    public RequestHelper getMockRequestHelper() {
        return Mockito.mock(RequestHelper.class);
    }
}
