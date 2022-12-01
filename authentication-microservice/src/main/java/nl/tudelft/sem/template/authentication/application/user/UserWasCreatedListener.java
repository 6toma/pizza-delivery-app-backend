package nl.tudelft.sem.template.authentication.application.user;

import nl.tudelft.sem.template.authentication.domain.user.UserWasCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This event listener is automatically called when a domain entity is saved
 * which has stored events of type: UserWasCreated.
 */
@Component
public class UserWasCreatedListener {
    Logger logger = LoggerFactory.getLogger(UserWasCreatedEvent.class);

    /**
     * The name of the function indicated which event is listened to.
     * The format is onEVENTNAME.
     *
     * @param event The event to react to
     */
    @EventListener
    public void onAccountWasCreated(UserWasCreatedEvent event) {
        // Handler code here
        logger.info("Account with netID: " + event.getNetId().toString() + " was created.");
    }
}
