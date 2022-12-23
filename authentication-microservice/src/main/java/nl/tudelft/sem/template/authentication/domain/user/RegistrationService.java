package nl.tudelft.sem.template.authentication.domain.user;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.NetIdAlreadyInUseException;
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
     * @param netId    The NetID of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(NetId netId, Password password) throws Exception {

        if (checkNetIdIsUnique(netId)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(netId, hashedPassword);
            userRepository.save(user);

            return user;
        }
        throw new NetIdAlreadyInUseException(netId);
    }

    public boolean checkNetIdIsUnique(NetId netId) {
        return !userRepository.existsByNetId(netId);
    }
}
