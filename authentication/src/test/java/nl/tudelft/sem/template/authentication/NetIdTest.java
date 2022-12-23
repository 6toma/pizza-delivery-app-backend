package nl.tudelft.sem.template.authentication;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NetIdTest {

    @Test
    void testEmptyConstructor() {
        var netId = new NetId();
        assertNull(netId.toString());
    }

    @Test
    void testToString() {
        var netIdString = "test@user.com";
        var netId = new NetId(netIdString);
        assertEquals(netIdString, netId.toString());
    }
}