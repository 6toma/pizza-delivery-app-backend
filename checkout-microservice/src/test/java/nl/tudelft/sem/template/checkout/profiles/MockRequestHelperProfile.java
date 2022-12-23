package nl.tudelft.sem.template.checkout.profiles;

import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Profile that overrides the original RequestHelper bean with a mocked RequestHelper bean. This helps when performing
 * integration tests that interact with the request helper.
 */
@Profile("mockRequestHelper")
@Configuration
public class MockRequestHelperProfile {

    /**
     * Mocks the RequestHelper.
     *
     * @return A mocked RequestHelper.
     */
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an AuthenticationManager
    public RequestHelper getMockRequestHelper() {
        return Mockito.mock(RequestHelper.class);
    }

}
