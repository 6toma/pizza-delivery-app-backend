package nl.tudelft.sem.template.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.HashedPassword;
import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import nl.tudelft.sem.template.authentication.framework.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authentication.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.authentication.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.authentication.models.RegistrationRequestModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder", "mockTokenGenerator", "mockAuthenticationManager", "mockRequestHelper"})
@ComponentScan("nl.tudelft.sem.template.commons")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UsersTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient JwtTokenGenerator mockJwtTokenGenerator;

    @Autowired
    private transient AuthenticationManager mockAuthenticationManager;

    @Autowired
    private transient UserRepository userRepository;
    @Autowired
    private transient RequestHelper mockRequestHelper;

    @Test
    public void register_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final NetId testUser = new NetId("someUser@gmail.com");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setNetId(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(model)));

        // Assert
        var result = resultActions.andExpect(status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Registration successful");

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
        verify(mockRequestHelper, times(1)).doRequest(any(), any(), any());
    }

    @Test
    public void register_withExistingUser_throwsException() throws Exception {
        // Arrange
        final NetId testUser = new NetId("someUser@gmail.com");
        final Password newTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");

        AppUser existingAppUser = new AppUser(testUser, existingTestPassword);
        userRepository.save(existingAppUser);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setNetId(testUser);
        model.setPassword(newTestPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
    }

    @Test
    public void login_withValidUser_returnsToken() throws Exception {
        // Arrange
        final NetId testUser = new NetId("someUser@gmail.com");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
            !testUser.toString().equals(authentication.getPrincipal())
                || !testPassword.toString().equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        final String testToken = "testJWTToken";
        when(mockJwtTokenGenerator.generateToken(
            argThat(userDetails -> userDetails.getUsername().equals(testUser.toString())))
        ).thenReturn(testToken);

        AppUser appUser = new AppUser(testUser, testHashedPassword);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setNetId(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(model)));


        // Assert
        MvcResult result = resultActions
            .andExpect(status().isOk())
            .andReturn();

        AuthenticationResponseModel responseModel = JsonUtil.deserialize(result.getResponse().getContentAsString(),
            AuthenticationResponseModel.class);

        assertThat(responseModel.getToken()).isEqualTo(testToken);

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
            testUser.toString().equals(authentication.getPrincipal())
                && testPassword.toString().equals(authentication.getCredentials())));
    }

    @Test
    public void login_withNonexistentUsername_returns403() throws Exception {
        // Arrange
        final String testUser = "someUser@gmail.com";
        final String testPassword = "password123";

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
            testUser.equals(authentication.getPrincipal())
                && testPassword.equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setNetId(new NetId(testUser));
        model.setPassword(new Password(testPassword));

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
            testUser.equals(authentication.getPrincipal())
                && testPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }

    @Test
    public void login_withInvalidPassword_returns403() throws Exception {
        // Arrange
        final var testUser = new NetId("someUser@gmail.com");
        final var wrongPassword = new Password("password1234");
        final var testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
            testUser.toString().equals(authentication.getPrincipal())
                && wrongPassword.toString().equals(authentication.getCredentials())
        ))).thenThrow(new BadCredentialsException("Invalid password"));

        AppUser appUser = new AppUser(testUser, testHashedPassword);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setNetId(testUser);
        model.setPassword(wrongPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isUnauthorized());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
            testUser.toString().equals(authentication.getPrincipal())
                && wrongPassword.toString().equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }

    @Test
    public void testLoginDisabledAccount() throws Exception {
        final String testUser = "someUser@gmail.com";
        final String wrongPassword = "password1234";
        final String testPassword = "password123";
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(new Password(testPassword))).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
            testUser.equals(authentication.getPrincipal())
                && wrongPassword.equals(authentication.getCredentials())
        ))).thenThrow(new DisabledException("This account is disabled"));

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setNetId(new NetId(testUser));
        model.setPassword(new Password(wrongPassword));

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(model)));

        resultActions.andExpect(status().isUnauthorized());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
            testUser.equals(authentication.getPrincipal())
                && wrongPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }
}
