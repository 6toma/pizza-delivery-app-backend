package nl.tudelft.sem.template.authentication.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HashedPasswordTest {
    @Test
    public void testToString(){
        HashedPassword hashedPassword = new HashedPassword("hash");
        assertEquals(hashedPassword.toString(),"hash");
    }
}
