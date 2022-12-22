package nl.tudelft.sem.template.coupon.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CouponTypeConverterTest {

    private CouponTypeConverter ctc = new CouponTypeConverter();

    @Test
    void convertToDatabaseColumn() {
        assertEquals("DISCOUNT", ctc.convertToDatabaseColumn(CouponType.DISCOUNT));
    }

    @Test
    void convertToEntityAttribute() {
        assertEquals(CouponType.ONE_PLUS_ONE, ctc.convertToEntityAttribute("ONE_PLUS_ONE"));
    }
}