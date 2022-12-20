package nl.tudelft.sem.template.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtTokenVerifierTests {
    private transient JwtTokenVerifier jwtTokenVerifier;

    private final String secret = "testSecret123";

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        jwtTokenVerifier = new JwtTokenVerifier();
        this.injectSecret(secret);
    }

    @Test
    public void validateNonExpiredToken() {
        // Arrange
        String token = generateToken(secret, "user123", UserRole.CUSTOMER, -10_000_000, 10_000_000);

        // Act
        boolean actual = jwtTokenVerifier.validateToken(token);

        // Assert
        assertThat(actual).isTrue();
    }

    @Test
    public void validateExpiredToken() {
        // Arrange
        String token = generateToken(secret, "user123", UserRole.CUSTOMER, -10_000_000, -5_000_000);

        // Act
        ThrowableAssert.ThrowingCallable action = () -> jwtTokenVerifier.validateToken(token);

        // Assert
        assertThatExceptionOfType(ExpiredJwtException.class)
            .isThrownBy(action);
    }

    @Test
    public void validateTokenIncorrectSignature() {
        // Arrange
        String token = generateToken("incorrectSecret", "user123", UserRole.CUSTOMER, -10_000_000, 10_000_000);

        // Act
        ThrowableAssert.ThrowingCallable action = () -> jwtTokenVerifier.validateToken(token);

        // Assert
        assertThatExceptionOfType(SignatureException.class)
            .isThrownBy(action);
    }

    @Test
    public void validateMalformedToken() {
        // Arrange
        String token = "malformedtoken";

        // Act
        ThrowableAssert.ThrowingCallable action = () -> jwtTokenVerifier.validateToken(token);

        // Assert
        assertThatExceptionOfType(MalformedJwtException.class)
            .isThrownBy(action);
    }

    @Test
    public void parseEmail() {
        // Arrange
        String expected = "user123";
        String token = generateToken(secret, expected, UserRole.CUSTOMER, -10_000_000, 10_000_000);

        // Act
        String actual = jwtTokenVerifier.getEmailFromToken(token);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void parseRole() {
        var role = UserRole.REGIONAL_MANAGER;
        String token = generateToken(secret, "user123", role, -10_000_000, 10_000_000);
        String actual = jwtTokenVerifier.getRoleFromToken(token);
        assertThat(actual).isEqualTo(role.getJwtRoleName());
    }

    private String generateToken(String jwtSecret, String email, UserRole role, long issuanceOffset, long expirationOffset) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.getJwtRoleName());
        return Jwts.builder().setClaims(claims).setSubject(email)
            .setIssuedAt(new Date(System.currentTimeMillis() + issuanceOffset))
            .setExpiration(new Date(System.currentTimeMillis() + expirationOffset))
            .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = jwtTokenVerifier.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(jwtTokenVerifier, secret);
    }
}
