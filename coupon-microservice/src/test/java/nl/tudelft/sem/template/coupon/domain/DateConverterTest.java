package nl.tudelft.sem.template.coupon.domain;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DateConverterTest {

    private DateConverter dc = new DateConverter();

    @SneakyThrows
    @Test
    void convertToDatabaseColumn() {
        Date date = new Date(16, 12, 2022);
        assertEquals("16/12/2022", dc.convertToDatabaseColumn(date));
    }

    @SneakyThrows
    @Test
    void convertToEntityAttribute() {
        Date date = new Date(16, 12, 2022);
        assertEquals(date, dc.convertToEntityAttribute("16/12/2022"));
    }

    @Test
    void convertToEntityAttributeException() {
        assertThrows(NumberFormatException.class, () -> dc.convertToEntityAttribute("Hello"));
    }
}