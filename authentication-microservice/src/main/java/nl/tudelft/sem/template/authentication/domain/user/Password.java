package nl.tudelft.sem.template.authentication.domain.user;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a password in our domain.
 */
@EqualsAndHashCode
public class Password {
    private final transient String passwordValue;

    public Password(String password) {
        // Validate input
        this.passwordValue = password;
    }

    @JsonValue
    @Override
    public String toString() {
        return passwordValue;
    }
}
