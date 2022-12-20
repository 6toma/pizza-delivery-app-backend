package nl.tudelft.sem.template.authentication.domain.user;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.UserEmail;
import nl.tudelft.sem.template.authentication.EmailAlreadyInUseException;
import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final transient UserRepository userRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Register a new user.
     *
     * @param userEmail    The email of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(UserEmail userEmail, Password password) throws Exception {

        if (checkEmailIsUnique(userEmail)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(userEmail, hashedPassword);
            userRepository.save(user);

            return user;
        }
        throw new EmailAlreadyInUseException(userEmail);
    }

    public boolean checkEmailIsUnique(UserEmail userEmail) {
        return !userRepository.existsByEmail(userEmail);
    }
}
