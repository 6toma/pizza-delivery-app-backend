package nl.tudelft.sem.template.authentication.application.user;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.UserWasCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class UserWasCreatedListenerTest {

    private Logger logger;
    private UserWasCreatedListener listener;

    @BeforeEach
    void setup() {
        this.logger = mock(Logger.class);
        this.listener = new UserWasCreatedListener(logger);
    }

    @Test
    void onAccountWasCreated() {
        listener.onAccountWasCreated(new UserWasCreatedEvent(new NetId("user@gmail.com")));
        verify(logger, times(1)).info(anyString());
    }
}