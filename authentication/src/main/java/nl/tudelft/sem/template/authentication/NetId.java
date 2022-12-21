package nl.tudelft.sem.template.authentication;

import java.io.Serializable;
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

    public NetId(String netId) {
        // validate NetID
        this.netIdValue = netId;
    }

    @Override
    public String toString() {
        return netIdValue;
    }
}
