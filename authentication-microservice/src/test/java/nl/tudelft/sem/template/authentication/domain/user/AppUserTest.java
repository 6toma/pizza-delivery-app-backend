package nl.tudelft.sem.template.authentication.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.authentication.UserEmail;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void testChangePassword() {
        var email = new UserEmail("user@gmail.com");
        var password = new HashedPassword("password_hash");
        var user = new AppUser(email, password);
        assertEquals(password, user.getPassword());
        var newPassword = new HashedPassword("password_hash_2");
        user.changePassword(newPassword);
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void testChangePasswordNull() {
        var email = new UserEmail("user@gmail.com");
        var password = new HashedPassword("password_hash");
        var user = new AppUser(email, password);
        assertEquals(password, user.getPassword());
        assertThrows(NullPointerException.class, () -> user.changePassword(null));
        assertEquals(password, user.getPassword());
    }

    @Test
    void testEquals() {
        var email = new UserEmail("user@gmai.com");
        var user1 = new AppUser(email, UserRole.CUSTOMER, new HashedPassword("password_1"));
        var user2 = new AppUser(email, UserRole.REGIONAL_MANAGER, new HashedPassword("password_0"));
        assertEquals(user1, user2);
    }

    @Test
    void testHashCode() {
        var email = new UserEmail("user@gmail.com");
        var user1 = new AppUser(email, UserRole.CUSTOMER, new HashedPassword("password_1"));
        var user2 = new AppUser(email, UserRole.REGIONAL_MANAGER, new HashedPassword("password_0"));
        assertEquals(user1.hashCode(), user2.hashCode());
    }

}