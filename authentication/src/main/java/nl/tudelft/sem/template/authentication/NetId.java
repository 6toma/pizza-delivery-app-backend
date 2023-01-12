package nl.tudelft.sem.template.authentication;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
public class NetId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String netIdValue;

    /**
     * Creates a new NetId instance with a specific net id which should be an email address.
     *
     * @param netId The net id (email address).
     */
    public NetId(String netId) {
        // validate NetID
        this.netIdValue = netId;
        if (!checkEmail(netId)) {
            throw new EmailNotValidException("The email '" + this.netIdValue + "' is not valid.");
        }
    }

    private boolean checkEmail(String email) {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @JsonValue
    @Override
    public String toString() {
        return netIdValue;
    }
}
