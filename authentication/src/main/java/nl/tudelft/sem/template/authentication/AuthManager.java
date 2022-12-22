package nl.tudelft.sem.template.authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Authentication Manager.
 */
@Component
public class AuthManager {

    /**
     * Interfaces with spring security to get the name of the user in the current context.
     *
     * @return The name of the user.
     */
    public String getNetId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Gets the <code>NetId</code> object of the current authenticated user.
     *
     * @return a full NetId object of the authenticated user
     */
    public NetId getNetIdObject() {
        return new NetId(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Gets the role of the user in the format ROLE_rolename.
     *
     * @return the role of the user a String
     */
    public String getRole() {
        var firstAuthority =
            SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst();
        if (firstAuthority.isEmpty()) {
            throw new IllegalArgumentException("No role has been set in the UserDetails");
        }
        return firstAuthority.get().getAuthority();
    }
}
