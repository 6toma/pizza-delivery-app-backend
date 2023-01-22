package nl.tudelft.sem.template.authentication.domain.user;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import nl.tudelft.sem.template.authentication.NetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.event.EventListener;

@SpringBootTest
public class UserEventTest {

    private static final String userCreated = "userCreated@test.com";
    private static final String userPasswordChanged = "userPasswordChanged@test.com";
    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserEventListener eventListener;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.flush();
        Mockito.reset(eventListener);
    }

    @Test
    void testUserCreatedEventCalled() {
        var netId = new NetId(userCreated);
        var password = new HashedPassword("password_hash");
        var appUser = new AppUser(netId, password);

        // saving twice still has one event
        userRepository.save(appUser);
        userRepository.save(appUser);
        // if I comment out @AfterDomainEventPublication test will fail otherwise will pass

        verify(eventListener).onUserCreation(new UserWasCreatedEvent(netId));
    }

    @Test
    void testUserCreatedWithRoleEventCalled() {
        var netId = new NetId(userCreated);
        var password = new HashedPassword("password_hash");
        var appUser = new AppUser(netId, UserRole.CUSTOMER, password);

        // saving twice still has one event
        userRepository.save(appUser);

        verify(eventListener).onUserCreation(new UserWasCreatedEvent(netId));
    }

    @Test
    void testUserPasswordUpdateEventCalled() {
        var netId = new NetId(userPasswordChanged);
        var password = new HashedPassword("password_hash");
        var appUser = new AppUser(netId, password);
        appUser.changePassword(new HashedPassword("new_hashed_password"));

        userRepository.save(appUser);
        var passwordWasChanged = new PasswordWasChangedEvent(appUser);
        verify(eventListener).onPasswordWasChanged(passwordWasChanged);
        assertEquals(passwordWasChanged.getUser(), appUser);
    }


    @TestComponent
    public static class UserEventListener {

        @EventListener
        public void onUserCreation(UserWasCreatedEvent event) {
            System.out.println("Add user " + event.getNetId());
        }

        @EventListener
        public void onPasswordWasChanged(PasswordWasChangedEvent event) {
            System.out.println("Password here");
        }
    }

}
