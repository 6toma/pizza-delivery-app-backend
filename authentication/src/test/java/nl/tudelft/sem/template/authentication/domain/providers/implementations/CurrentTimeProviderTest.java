package nl.tudelft.sem.template.authentication.domain.providers.implementations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CurrentTimeProviderTest {

    @Test
    void getCurrentTime() {
        assertThat(new CurrentTimeProvider().getCurrentTime()).isNotNull();
    }
}