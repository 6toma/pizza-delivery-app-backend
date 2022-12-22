package nl.tudelft.sem.template.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthManagerTests {
    private transient AuthManager authManager;

    @BeforeEach
    public void setup() {
        authManager = new AuthManager();
    }

    @Test
    public void getEmailTest() {
        // Arrange
        String expected = "user123";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
            expected,
            null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        String actual = authManager.getEmail();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getEmailObjectTest() {

        String expected = "validEmail@gmail.com";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
            expected,
            null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        UserEmail actual = authManager.getEmailObject();

        // Assert
        assertThat(actual).isEqualTo(new UserEmail(expected));
    }

    @Test
    public void getRoleTest() {
        var role = UserRole.REGIONAL_MANAGER;
        var authenticationToken = new UsernamePasswordAuthenticationToken("user123", null,
            List.of(new SimpleGrantedAuthority(role.getJwtRoleName())));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        assertThat(authManager.getRole()).isEqualTo(role.getJwtRoleName());
    }

    @Test
    public void getRoleNotSpecifiedTest() {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
            "user123", null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        assertThrows(IllegalArgumentException.class, () -> authManager.getRole());
    }
}
