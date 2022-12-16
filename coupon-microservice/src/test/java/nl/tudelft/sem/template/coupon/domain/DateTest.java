package nl.tudelft.sem.template.coupon.domain;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class DateTest {

    @Test
    void constructorFalse() {
        assertThrows(ParseException.class, () -> new Date(31, 02, 2023));
    }

    @Test
    void constructor() {
        Date date = new Date(24, 05, 2023);
        assertEquals(24, date.getDay());
        assertEquals(05, date.getMonth());
        assertEquals(2023, date.hashCode());
    }

}