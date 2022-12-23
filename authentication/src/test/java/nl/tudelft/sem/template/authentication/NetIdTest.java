package nl.tudelft.sem.template.authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class NetIdTest {

    @Test
    void testEmptyConstructor() {
        var netId = new NetId();
        assertNull(netId.toString());
    }

    @Test
    void testToString() {
        var netIdString = "test@user.com";
        var netId = new NetId(netIdString);
        assertEquals(netIdString, netId.toString());
    }

    public static List<String> validEmails() {
        return List.of("emai@gmail.com", "a.b.c.d@yahoo.com", "email.1.2.3.5@hotmail.com",
            "email_with_underscore@mail.com", "EmailWithCapitalLetter@gmai.com");
    }

    public static List<String> invalidEmails() {
        return List.of("emailWithNoAt", "EmailWithIllegalChar$@mail.com", "EmailWithoutcom@gmail");
    }

    @MethodSource("validEmails")
    @ParameterizedTest
    public void checkValidEmail(String email) {
        new NetId(email);
    }

    @MethodSource("invalidEmails")
    @ParameterizedTest
    public void checkInvalidEmail(String email) {
        assertThatThrownBy(() -> new NetId(email)).isInstanceOf(EmailNotValidException.class);
    }


}