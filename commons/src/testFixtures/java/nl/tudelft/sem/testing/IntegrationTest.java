package nl.tudelft.sem.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import lombok.SneakyThrows;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.annotations.role.MicroServiceInteraction;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * <p>Helper class for integration testing your controllers. It sets up all the magic annotations for integration testing
 * and activates the right profiles. These profiles are used to override certain components (like {@link JwtTokenVerifier})
 * to a mocked version so you can control their return values.</p>
 * <p>It also adds some helper method to authentication your request and assert the response body of a request to your
 * endpoint.</p>
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "mockRequestHelper"})
@ComponentScan({"nl.tudelft.sem.testing.profiles"})
public class IntegrationTest {

    protected static final String TEST_USER = "test_user@gmail.com";

    protected final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected transient MockMvc mockMvc;
    @Autowired
    protected transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    protected transient AuthManager mockAuthenticationManager;
    @Autowired
    protected transient RequestHelper requestHelper;

    /**
     * Converts object to Json in order simulate a <i>real</i> post request.
     *
     * @param obj object to convert to json
     * @return a json representation of the object
     */
    public String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to assert that the body of a response is equal to some textual value.
     *
     * @param expected The text value you expect it to be
     * @param result   The http response
     * @throws UnsupportedEncodingException Thrown if body can't be encoded as a string
     */
    protected void assertResponseEqualsText(String expected, ResultActions result) throws UnsupportedEncodingException {
        assertThat(parseResponseText(result)).isEqualTo(expected);
    }

    protected String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    @SneakyThrows
    protected ResultActions doRequest(MockHttpServletRequestBuilder builder) {
        return mockMvc.perform(authenticated(builder));
    }

    /**
     * Attempts to parse the body of a http response as a java class. This assumes that the response body is json formatted
     * and can be parsed to the class specified. If not, an error will be thrown.
     *
     * @param result The http response
     * @param cls    The class you want to parse to
     * @param <T>    The type you want to parse
     * @return The parsed object
     * @throws IOException Thrown when there are errors related to reading the response body
     */
    protected <T> T parseResponseJson(ResultActions result, Class<T> cls) throws IOException {
        try (var inputStream = new ByteArrayInputStream(result.andReturn().getResponse().getContentAsByteArray())) {
            return mapper.readValue(inputStream, cls);
        }
    }

    protected String parseResponseText(ResultActions result) throws UnsupportedEncodingException {
        return result.andReturn().getResponse().getContentAsString();
    }

    /**
     * Adds authentication to a request by using the default netid (test_user) and the customer role.
     *
     * @param builder Your request
     * @return The modified builder with the authorization header included
     */
    protected MockHttpServletRequestBuilder authenticated(MockHttpServletRequestBuilder builder) {
        return authenticated(builder, TEST_USER);
    }

    /**
     * Adds authentication to a request by using a specific netid and the customer role.
     *
     * @param builder Your request
     * @param netId   The netid you want to use for authentication
     * @return The modified builder with the authorization header included
     */
    protected MockHttpServletRequestBuilder authenticated(MockHttpServletRequestBuilder builder, String netId) {
        return authenticated(builder, netId, UserRole.CUSTOMER);
    }

    /**
     * Adds authentication to a request by using the default net id (test_user) and the specified role.
     *
     * @param builder Your request
     * @param role    The role of the user you want to authenticate as
     * @return The modified builder with the authorization header included
     */
    protected MockHttpServletRequestBuilder authenticated(MockHttpServletRequestBuilder builder, UserRole role) {
        return authenticated(builder, TEST_USER, role);
    }

    /**
     * Helper method to authenticate a mock http request. It sets up the mock return values for the necessary methods for
     * authentication and adds the authorization header to your request.
     *
     * @param builder Your request
     * @param netId   The net id of the user you want to authenticate as
     * @param role    The role of the user you want to authenticate as
     * @return The modified builder with the authorization header included
     */
    protected MockHttpServletRequestBuilder authenticated(MockHttpServletRequestBuilder builder, String netId,
                                                          UserRole role) {
        when(mockAuthenticationManager.getRole()).thenReturn(role);
        return authenticated(builder, netId, role.getJwtRoleName());
    }

    protected MockHttpServletRequestBuilder microservice(MockHttpServletRequestBuilder builder) {
        return authenticated(builder, "email@gmail.com", MicroServiceInteraction.AUTHORITY);
    }

    protected MockHttpServletRequestBuilder authenticated(MockHttpServletRequestBuilder builder, String netId,
                                                          String role) {
        when(mockAuthenticationManager.getNetId()).thenReturn(netId);
        when(mockAuthenticationManager.getNetIdObject()).thenReturn(new NetId(netId));
        when(mockAuthenticationManager.getRoleAuthority()).thenReturn(role);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(netId);
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(role);
        return builder.header("Authorization", "Bearer SomeRandomToken");
    }
}
