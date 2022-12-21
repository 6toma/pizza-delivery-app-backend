package nl.tudelft.sem.template.authentication.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.HashedPassword;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class JwtUserDetailsServiceTest {

    private UserRepository userRepository;
    private JwtUserDetailsService userDetailsService;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new JwtUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsernameFound() {
        var netId = new NetId("user");
        var password = new HashedPassword("hashed_password");
        var role = UserRole.REGIONAL_MANAGER;
        var user = new AppUser(netId, role, password);
        when(userRepository.findByNetId(any())).thenReturn(Optional.of(user));

        var result = userDetailsService.loadUserByUsername("user");
        assertNotNull(result);
        assertEquals(netId.toString(), result.getUsername());
        assertEquals(password.toString(), result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertEquals(role.getJwtRoleName(), result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(userRepository.findByNetId(any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("username"));
    }
}