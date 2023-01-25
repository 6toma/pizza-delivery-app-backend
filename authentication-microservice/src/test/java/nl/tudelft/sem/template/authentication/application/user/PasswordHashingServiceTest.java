package nl.tudelft.sem.template.authentication.application.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashingServiceTest {

    @Test
    void testHashWorks() {
        PasswordHashingService passwordHashingService = new PasswordHashingService(new BCryptPasswordEncoder());
        assertNotNull(passwordHashingService.hash(new Password("password")));
    }
}
