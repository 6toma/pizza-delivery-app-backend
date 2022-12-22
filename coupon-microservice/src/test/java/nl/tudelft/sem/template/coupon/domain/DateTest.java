package nl.tudelft.sem.template.coupon.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class DateTest {

    @Test
    void constructorFalse() {
        assertThrows(ParseException.class, () -> new Date(31, 02, 2022));
    }

    @SneakyThrows
    @Test
    void constructor() {
        Date date = new Date(24, 05, 2023);
        assertEquals(24, date.getDay());
        assertEquals(05, date.getMonth());
        assertEquals(2023, date.getYear());
    }

}