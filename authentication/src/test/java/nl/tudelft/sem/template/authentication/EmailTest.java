package nl.tudelft.sem.template.authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class EmailTest {

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
        new UserEmail(email);
    }

    @MethodSource("invalidEmails")
    @ParameterizedTest
    public void checkInvalidEmail(String email) {
        assertThatThrownBy(() -> new UserEmail(email)).isInstanceOf(EmailNotValidException.class);
    }
}
