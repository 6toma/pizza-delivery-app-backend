package nl.tudelft.sem.template.authentication.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import nl.tudelft.sem.template.authentication.NetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

class AppUserTest {

    private NetId netId;
    private HashedPassword password;
    private AppUser user;

    class MockAppUser extends AppUser {

        private transient List<Object> domainEvents = new ArrayList<>();

        @Override
        public void recordThat(Object event) {
            if (domainEvents == null)
                domainEvents = new ArrayList<>();
            domainEvents.add(Objects.requireNonNull(event));
        }
    }

    @BeforeEach
    void setup() {
        netId = new NetId("user1@test.com");
        password = new HashedPassword("password_hash");
        user = new AppUser(netId, password);
    }

    @Test
    void changePassword() {
        MockAppUser he = Mockito.mock(MockAppUser.class);
        PasswordWasChangedEvent passwordWasChangedEvent = new PasswordWasChangedEvent(he);
        HashedPassword hp = new HashedPassword("test12%");
        doCallRealMethod().when(he).changePassword(hp);
        doCallRealMethod().when(he).recordThat(passwordWasChangedEvent);
        he.changePassword(hp);
        verify(he).recordThat(Mockito.any());
    }

    @Test
    void testChangePassword() {
        assertEquals(password, user.getPassword());
        var newPassword = new HashedPassword("password_hash_2");
        user.changePassword(newPassword);
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void testChangePasswordNull() {
        var netId = new NetId("user1@test.com");
        var password = new HashedPassword("password_hash");
        var user = new AppUser(netId, password);
        assertEquals(password, user.getPassword());
        assertThrows(NullPointerException.class, () -> user.changePassword(null));
        assertEquals(password, user.getPassword());
    }

    @ParameterizedTest
    @MethodSource("equalsCases")
    void testEquals(AppUser in1, Object in2, boolean expected) {
        assertThat(in1.equals(in2)).isEqualTo(expected);
    }

    @SneakyThrows
    static Stream<Arguments> equalsCases() {
        var netId = new NetId("user1@test.com");
        var user1 = new AppUser(netId, UserRole.CUSTOMER, new HashedPassword("password_1"));
        var user2 = new AppUser(netId, UserRole.REGIONAL_MANAGER, new HashedPassword("password_0"));
        var user3 = user1;
        var user4 = new AppUser(new NetId("user1@test.com"), UserRole.CUSTOMER, new HashedPassword("password_1"));
        return Stream.of(
            Arguments.of(user1, user1, true),
            Arguments.of(user1, user2, true),
            Arguments.of(user3, user1, true),
            Arguments.of(user1, user4, true)
        );
    }

    @Test
    void testHashCode() {
        var netId = new NetId("user1@test.com");
        var user1 = new AppUser(netId, UserRole.CUSTOMER, new HashedPassword("password_1"));
        var user2 = new AppUser(netId, UserRole.REGIONAL_MANAGER, new HashedPassword("password_0"));
        assertEquals(user1.hashCode(), user2.hashCode());
    }

}