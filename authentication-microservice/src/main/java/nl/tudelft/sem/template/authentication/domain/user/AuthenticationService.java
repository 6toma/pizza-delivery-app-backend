package nl.tudelft.sem.template.authentication.domain.user;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.authentication.JwtUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service that manages authentication for a user. Mainly it will handle logging in of a user.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final transient AuthenticationManager authenticationManager;
    private final transient JwtTokenGenerator jwtTokenGenerator;
    private final transient JwtUserDetailsService jwtUserDetailsService;

    /**
     * <p>Attempts to login a user using their netid and their password.</p>
     * <p>If the login attempt was successful this will return a newly generated JWT token that the user can use throughout
     * the system to identify themselves.</p>
     * <p>If the credentials were incorrect, or the user wasn't found, an error will be thrown.</p>
     *
     * @param netId    The netid of the user
     * @param password The password of the user
     * @return The generated JWT token for the user
     */
    public String loginUser(NetId netId, Password password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(netId.toString(), password.toString()));

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(netId.toString());
        return jwtTokenGenerator.generateToken(userDetails);
    }

}
