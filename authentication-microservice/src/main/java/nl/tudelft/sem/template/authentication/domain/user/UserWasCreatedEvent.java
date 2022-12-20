package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.UserEmail;

/**
 * A DDD domain event that indicated a user was created.
 */
public class UserWasCreatedEvent {
    private final UserEmail userEmail;

    public UserWasCreatedEvent(UserEmail userEmail) {
        this.userEmail = userEmail;
    }

    public UserEmail getEmail() {
        return this.userEmail;
    }
}
