package nl.tudelft.sem.template.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import nl.tudelft.sem.template.authentication.domain.providers.TimeProvider;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtTokenGeneratorTests {
    private transient JwtTokenGenerator jwtTokenGenerator;
    private transient TimeProvider timeProvider;
    private transient Instant mockedTime = Instant.parse("2021-12-31T13:25:34.00Z");

    private final String secret = "testSecret123";

    private String netId = "andy";
    private UserDetails user;

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        timeProvider = mock(TimeProvider.class);
        when(timeProvider.getCurrentTime()).thenReturn(mockedTime);

        jwtTokenGenerator = new JwtTokenGenerator(timeProvider);
        this.injectSecret(secret);

        user = User.withUsername(netId).password("someHash").authorities(UserRole.CUSTOMER.getJwtRoleName()).build();
    }

    @Test
    public void generatedTokenHasCorrectIssuanceDate() {
        // Act
        String token = jwtTokenGenerator.generateToken(user);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getIssuedAt()).isEqualTo(mockedTime.toString());
    }

    @Test
    public void generatedTokenHasCorrectExpirationDate() {
        // Act
        String token = jwtTokenGenerator.generateToken(user);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getExpiration()).isEqualTo(mockedTime.plus(1, ChronoUnit.DAYS).toString());
    }

    @Test
    public void generatedTokenHasCorrectNetId() {
        // Act
        String token = jwtTokenGenerator.generateToken(user);

        // Assert
        Claims claims = getClaims(token);
        assertThat(claims.getSubject()).isEqualTo(netId);
    }

    @Test
    public void generatedTokenHasCorrectRole() {
        String role = user.getAuthorities().stream().findFirst().get().getAuthority();
        String token = jwtTokenGenerator.generateToken(user);

        Claims claims = getClaims(token);
        assertThat(claims.get("role")).isEqualTo(role);
    }

    @Test
    public void generateTokenNoRole() {
        var user = User.withUsername(netId).password("someHash").authorities(new String[0]).build();
        assertThrows(IllegalArgumentException.class, () -> jwtTokenGenerator.generateToken(user));
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setAllowedClockSkewSeconds(Integer.MAX_VALUE).setSigningKey(secret).parseClaimsJws(token)
            .getBody();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = jwtTokenGenerator.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(jwtTokenGenerator, secret);
    }
}
