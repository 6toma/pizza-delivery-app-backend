package nl.tudelft.sem.template.authentication;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DDD value object representing a email in our domain.
 */
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
public class UserEmail implements Serializable {

    private String emailValue;

    public UserEmail(String email) {
        if (!checkEmail(email)) {
            throw new EmailNotValidException("The email '" + email + "' is not valid.");
        }
        // validate email
        this.emailValue = email;
    }

    private boolean checkEmail(String email) {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return emailValue;
    }
}
