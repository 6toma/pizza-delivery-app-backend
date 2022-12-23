package nl.tudelft.sem.template.authentication.dataloaders;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreOwnerDataLoader implements ApplicationRunner {

    private static final String STORE_OWNER_PASSWORD = "password123";
    private static final NetId STORE_OWNER_NET_ID_1 = new NetId("storeowner1@ap.nl");
    private static final NetId STORE_OWNER_NET_ID_2 = new NetId("storeowner2@ap.nl");
    private static final NetId STORE_OWNER_NET_ID_3 = new NetId("storeowner3@ap.nl");

    private final UserRepository userRepository;
    private final PasswordHashingService hashingService;


    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findAllByRole(UserRole.STORE_OWNER).isEmpty()) {
            var hashedPassword = hashingService.hash(new Password(STORE_OWNER_PASSWORD));
            // Will only be null when hashingService is mocked. This is useful for tests,
            // so you won't have to return a value in the mock for a lot of requests.
            if (hashedPassword != null) {
                var user1 = new AppUser(STORE_OWNER_NET_ID_1, UserRole.STORE_OWNER, hashedPassword);
                var user2 = new AppUser(STORE_OWNER_NET_ID_2, UserRole.STORE_OWNER, hashedPassword);
                var user3 = new AppUser(STORE_OWNER_NET_ID_3, UserRole.STORE_OWNER, hashedPassword);
                userRepository.saveAll(Arrays.asList(user1, user2, user3));
            }
        }

    }
}