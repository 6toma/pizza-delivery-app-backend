package nl.tudelft.sem.template.authentication.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebSecurityConfigTest {
    @Autowired
    WebSecurityConfig webSecurityConfig;

    @Test
    void testEncoderNotNull() {
        assertNotNull(webSecurityConfig.passwordEncoder());
    }

    @Test
    void testPasswordHashEncoder() {
        assertNotNull(webSecurityConfig.passwordHashEncoder());
    }
}
