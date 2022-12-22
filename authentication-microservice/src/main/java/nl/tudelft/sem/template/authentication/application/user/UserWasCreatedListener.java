package nl.tudelft.sem.template.authentication.application.user;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.domain.user.UserWasCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * This event listener is automatically called when a domain entity is saved which has stored events of type:
 * UserWasCreated.
 */
@Component
@RequiredArgsConstructor
public class UserWasCreatedListener {

    private final Logger logger;

    /**
     * Creates a new UserWasCreatedListener with a default logger.
     */
    public UserWasCreatedListener() {
        this.logger = LoggerFactory.getLogger(UserWasCreatedListener.class);
    }

    /**
     * The name of the function indicated which event is listened to. The format is onEVENTNAME.
     *
     * @param event The event to react to
     */
    @EventListener
    public void onAccountWasCreated(UserWasCreatedEvent event) {
        // Handler code here
        logger.info("Account with email: " + event.getEmail().toString() + " was created.");
    }
}
