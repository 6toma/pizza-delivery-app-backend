package nl.tudelft.sem.template.authentication.domain.user;

/**
 * Enum used to specify what the role of a user is.
 */
public enum UserRole {
    CUSTOMER,
    STORE_OWNER,
    REGIONAL_MANAGER;

    /**
     * Returns the value that should be included as a granted authority in the JWT token.
     * The format we've chosen is ROLE_{NAME_OF_ENUM_VALUE}.
     *
     * @return The JWT granted authority role name
     */
    public String getJwtRoleName() {
        return "ROLE_" + name();
    }
}