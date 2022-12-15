package nl.tudelft.sem.template.authentication.domain.user;

import nl.tudelft.sem.template.authentication.NetId;

/**
 * A DDD domain event that indicated a user was created.
 */
public class UserWasCreatedEvent {
    private final NetId netId;

    public UserWasCreatedEvent(NetId netId) {
        this.netId = netId;
    }

    public NetId getNetId() {
        return this.netId;
    }
}
