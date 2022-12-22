package nl.tudelft.sem.template.authentication.profiles;

import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockRequestHelper")
@Configuration
public class MockRequestHelperProfile {

    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject a TokenGenerator
    public RequestHelper getMockRequestHelper() {
        return Mockito.mock(RequestHelper.class);
    }
}
