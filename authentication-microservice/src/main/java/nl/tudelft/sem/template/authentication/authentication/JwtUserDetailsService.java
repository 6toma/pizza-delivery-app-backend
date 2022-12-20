package nl.tudelft.sem.template.authentication.authentication;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User details service responsible for retrieving the user from the DB.
 */
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final transient UserRepository userRepository;

    /**
     * Loads user information required for authentication from the DB.
     *
     * @param username The username of the user we want to authenticate
     * @return The authentication user information of that user
     * @throws UsernameNotFoundException Username was not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optionalUser = userRepository.findByNetId(new NetId(username));

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User does not exist");
        }

        var user = optionalUser.get();

        return User.withUsername(user.getNetId().toString())
            .password(user.getPassword().toString())
            .authorities(new SimpleGrantedAuthority(user.getRole().getJwtRoleName()))
            .build();
    }
}
