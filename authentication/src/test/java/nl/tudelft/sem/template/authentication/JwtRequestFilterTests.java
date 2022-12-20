package nl.tudelft.sem.template.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtRequestFilterTests {
    private transient JwtRequestFilter jwtRequestFilter;

    private transient HttpServletRequest mockRequest;
    private transient HttpServletResponse mockResponse;
    private transient FilterChain mockFilterChain;

    private transient JwtTokenVerifier mockJwtTokenVerifier;
    private transient AuthManager authManager;

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockFilterChain = Mockito.mock(FilterChain.class);
        mockJwtTokenVerifier = Mockito.mock(JwtTokenVerifier.class);
        authManager = Mockito.mock(AuthManager.class);

        jwtRequestFilter = new JwtRequestFilter(mockJwtTokenVerifier, authManager);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @AfterEach
    public void assertChainContinues() throws ServletException, IOException {
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verifyNoMoreInteractions(mockFilterChain);
    }

    @Test
    public void correctToken() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getEmailFromToken(token)).thenReturn(user);
        when(mockJwtTokenVerifier.getRoleFromToken(token)).thenReturn(UserRole.CUSTOMER.getJwtRoleName());

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getName())
            .isEqualTo(user);
        assertThat(authentication.getAuthorities().size()).isEqualTo(1);
        assertThat(authentication.getAuthorities().stream().findFirst().get().getAuthority()).isEqualTo(
            UserRole.CUSTOMER.getJwtRoleName());
    }

    @Test
    public void invalidToken() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(false);
        when(mockJwtTokenVerifier.getEmailFromToken(token)).thenReturn(user);
        when(mockJwtTokenVerifier.getRoleFromToken(token)).thenReturn(UserRole.CUSTOMER.getJwtRoleName());

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
            .isNull();
    }

    /**
     * This test verifies that a user will not be authenticated when they send a JWT token without a user role.
     *
     * @throws ServletException Servlet exception
     * @throws IOException Any exception related to input/output
     */
    @Test
    public void tokenVerificationNoRoleClaim() throws ServletException, IOException {
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getEmailFromToken(token)).thenReturn(user);
        when(mockJwtTokenVerifier.getRoleFromToken(token)).thenReturn(null);

        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    /**
     * Parameterized test for various token verification exceptions.
     *
     * @param throwable the exception to be tested
     */
    @ParameterizedTest
    @MethodSource("tokenVerificationExceptionGenerator")
    public void tokenVerificationException(Class<? extends Throwable> throwable)
        throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenThrow(throwable);
        when(mockJwtTokenVerifier.getEmailFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
            .isNull();
    }

    private static Stream<Arguments> tokenVerificationExceptionGenerator() {
        return Stream.of(
            Arguments.of(ExpiredJwtException.class),
            Arguments.of(IllegalArgumentException.class),
            Arguments.of(JwtException.class)

        );
    }

    @Test
    public void nullToken() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
            .isNull();
    }

    @Test
    public void invalidPrefix() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer1 " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getEmailFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
            .isNull();
    }

    @Test
    public void noPrefix() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn(token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getEmailFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
            .isNull();
    }
}
