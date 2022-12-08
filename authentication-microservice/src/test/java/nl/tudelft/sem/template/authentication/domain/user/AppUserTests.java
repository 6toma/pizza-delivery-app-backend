package nl.tudelft.sem.template.authentication.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;


public class AppUserTests {
    private final AppUser appUser1 = new AppUser(new NetId("same"), new HashedPassword("pass"));
    private final AppUser appUser2 = new AppUser(new NetId("same"), new HashedPassword("other pass"));
    private final AppUser appUser3 = new AppUser(new NetId("other"), new HashedPassword("other pass"));

    @Test
    void testEquals() {
        // Equals happens based on generated id from the database
        assertEquals(appUser1, appUser2);
        assertEquals(appUser1, appUser3);
        assertEquals(appUser1, appUser3);

        appUser1.setId(1);
        appUser2.setId(2);
        assertNotEquals(appUser1, appUser2);
    }

    @Test
    void testHashCode() {
        assertEquals(appUser1.hashCode(), appUser2.hashCode(), "hash code should be the the same");

        assertNotEquals(appUser1.hashCode(), appUser3.hashCode(), "hash code should be different");
    }


    @Test
    void testChangePassword() {
        AppUser appUser = new AppUser(new NetId("1"), new HashedPassword("old password"));
        var password = new HashedPassword("new password");

        appUser.changePassword(password);
        assertEquals(password, appUser.getPassword());
    }
}
