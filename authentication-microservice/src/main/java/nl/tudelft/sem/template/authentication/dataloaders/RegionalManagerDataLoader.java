package nl.tudelft.sem.template.authentication.dataloaders;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.NetId;
import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Data loader that checks whether a user with the regional manager role exists. If it does, no further action is needed. If
 * it doesn't it will create a default regional manager user that can be used to log in.
 */
@Component
@RequiredArgsConstructor
public class RegionalManagerDataLoader implements ApplicationRunner {

    private static final String REGIONAL_MANAGER_PASSWORD = "password123";
    private static final NetId REGIONAL_MANAGER_NET_ID = new NetId("regionalmanager@ap.nl");

    private final UserRepository userRepository;
    private final PasswordHashingService hashingService;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findAllByRole(UserRole.REGIONAL_MANAGER).isEmpty()) {
            var hashedPassword = hashingService.hash(new Password(REGIONAL_MANAGER_PASSWORD));
            // Will only be null when hashingService is mocked. This is useful for tests,
            // so you won't have to return a value in the mock for a lot of requests.
            if (hashedPassword != null) {
                var user = new AppUser(REGIONAL_MANAGER_NET_ID, UserRole.REGIONAL_MANAGER, hashedPassword);
                userRepository.save(user);
            }
        }
    }
}
