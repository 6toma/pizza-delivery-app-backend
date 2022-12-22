package nl.tudelft.sem.template.authentication.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.authentication.annotations.role.MicroServiceInteraction;
import nl.tudelft.sem.template.authentication.annotations.role.RoleRegionalManager;
import nl.tudelft.sem.template.authentication.annotations.role.RoleStoreOwner;
import nl.tudelft.sem.template.authentication.config.TestServerConfig;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = TestServerConfig.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
public class RoleAuthorizationTests {

    /**
     * Since this module is a library, no controllers (and endpoints) are included here. We still want to be to test whether
     * our role annotations work correctly, so we create a test controller with the endpoints we require for testing.
     */
    @RestController
    @RequestMapping("role_authorization")
    static class TestController {

        @GetMapping("/customer")
        void customerEndpoint() {

        }

        /**
         * Test endpoint that should only be allowed to be accessed by a store owner.
         */
        @GetMapping("/store_owner")
        @RoleStoreOwner
        void storeOwnerEndpoint() {
        }

        /**
         * Test endpoint that should only be allowed to be accessed by a regional manager.
         */
        @GetMapping("/regional_manager")
        @RoleRegionalManager
        void regionalManagerEndpoint() {
        }

        @GetMapping("/microservice")
        @MicroServiceInteraction
        void microServiceEndpoint() {
        }
    }

    @Autowired
    TestController controller;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    AuthManager mockAuthManager;

    @BeforeEach
    void setup() {
        when(mockAuthManager.getEmail()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getEmailFromToken(anyString())).thenReturn("ExampleUser");
    }

    /**
     * Tests whether an endpoint without any role annotations is accessible to a customer.
     *
     * @throws Exception Any exception that might occur while performing the request
     */
    @Test
    void testCustomerAuthorized() throws Exception {
        testAccess("/role_authorization/customer", UserRole.CUSTOMER, status().isOk());
    }

    /**
     * Tests whether an endpoint that has the {@link RoleStoreOwner} annotation is <b>not</b> accessible to a customer or a
     * regional manager.
     *
     * @param role The role it shouldn't be accessible to
     * @throws Exception Any exception that might occur while performing the request
     */
    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"CUSTOMER", "REGIONAL_MANAGER"})
    void testStoreOwnerUnauthorized(UserRole role) throws Exception {
        testAccess("/role_authorization/store_owner", role, status().isForbidden());
    }

    /**
     * Tests whether an endpoint that should be protected for store owners only is accessibly by a store owner.
     *
     * @throws Exception Any exception that might occur while performing the request
     */
    @Test
    void testStoreOwnerAuthorized() throws Exception {
        testAccess("/role_authorization/store_owner", UserRole.STORE_OWNER, status().isOk());
    }

    /**
     * Tests whether an endpoint that should be protected for regional managers isn't accessible by any other role.
     *
     * @param role The role it shouldn't be accessible to
     * @throws Exception Any exception that might occur while performing the request
     */
    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"CUSTOMER", "STORE_OWNER"})
    void testRegionalManagerUnauthorized(UserRole role) throws Exception {
        testAccess("/role_authorization/regional_manager", role, status().isForbidden());
    }

    /**
     * Tests whether an endpoint that is protected for regional managers is accessible by a regional manager.
     *
     * @throws Exception Any exception that might occur while performing the request
     */
    @Test
    void testRegionalManagerAuthorized() throws Exception {
        testAccess("/role_authorization/regional_manager", UserRole.REGIONAL_MANAGER, status().isOk());
    }

    @Test
    void testMicroServiceInteraction() throws Exception {
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(MicroServiceInteraction.AUTHORITY);
        mockMvc.perform(get("/role_authorization/microservice")
                .header("Authorization", "Bearer MockToken"))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"CUSTOMER", "STORE_OWNER", "REGIONAL_MANAGER"})
    void testMicroServiceInteractionUnauthorized(UserRole role) throws Exception {
        testAccess("/role_authorization/microservice", role, status().isForbidden());
    }

    /**
     * Helper method to test whether a specific user role has access (or not) to a specific endpoint.
     *
     * @param endpoint The endpoint to test
     * @param role     The role of the user to test with
     * @param expect   The expected result of the request (usually either status().ok() or status().forbidden())
     * @throws Exception Any exception that might occur while performing the request
     */
    private void testAccess(String endpoint, UserRole role, ResultMatcher expect) throws Exception {
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(role.getJwtRoleName());

        mockMvc.perform(get(endpoint)
                .header("Authorization", "Bearer MockedToken"))
            .andExpect(expect);
    }

}
