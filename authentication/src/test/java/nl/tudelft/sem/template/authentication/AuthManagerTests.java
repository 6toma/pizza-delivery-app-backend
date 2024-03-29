package nl.tudelft.sem.template.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthManagerTests {
    private transient AuthManager authManager;

    @BeforeEach
    void setup() {
        authManager = new AuthManager();
    }

    @Test
    void getNetidTest() {
        // Arrange
        String expected = "user123";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
            expected,
            null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        String actual = authManager.getNetId();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getNetIdObjectTest() {
        String expected = "user123@gmail.com";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
            expected,
            null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        var actual = authManager.getNetIdObject();

        // Assert
        assertThat(actual).isEqualTo(new NetId(expected));

    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class)
    void getRoleTest(UserRole role) {
        var authenticationToken = new UsernamePasswordAuthenticationToken("user123", null,
            List.of(new SimpleGrantedAuthority(role.getJwtRoleName())));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        assertThat(authManager.getRole()).isEqualTo(role);
    }

    @Test
    void getRoleAuthorityTest() {
        var role = UserRole.REGIONAL_MANAGER;
        var authenticationToken = new UsernamePasswordAuthenticationToken("user123", null,
            List.of(new SimpleGrantedAuthority(role.getJwtRoleName())));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        assertThat(authManager.getRoleAuthority()).isEqualTo(role.getJwtRoleName());
    }

    @Test
    void getRoleAuthorityNotSpecifiedTest() {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
            "user123", null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        assertThrows(IllegalArgumentException.class, () -> authManager.getRoleAuthority());
    }
}
