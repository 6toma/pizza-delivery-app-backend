package nl.tudelft.sem.template.coupon.domain;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponTest {

    private Coupon c1;
    private final static LocalDate LOCAL_DATE = LocalDate.of(2022, 12, 13);

    @InjectMocks
    private Coupon coupon;

    @Mock
    private Clock clock;
    private Clock fixedClock;

    @SneakyThrows
    @BeforeEach
    void setup() {
        c1 = new Coupon();
        c1.setCode("ABCD12");
        c1.setStoreId(1L);
        c1.setPercentage(10);
        c1.setType(CouponType.DISCOUNT);
        c1.setExpiryDate(new Date(10, 10, 2023));
        MockitoAnnotations.openMocks(this);

        //tell tests to return the specified LOCAL_DATE when calling LocalDate.now(clock)
        fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @ParameterizedTest
    @MethodSource("equalsCases")
    void equals(Coupon in1, Object in2, boolean expected) {
        assertThat(in1.equals(in2)).isEqualTo(expected);
    }

    @SneakyThrows
    static Stream<Arguments> equalsCases() {
        Coupon c1 = new Coupon();
        c1.setCode("ABCD12");
        c1.setStoreId(1L);
        c1.setPercentage(10);
        c1.setType(CouponType.DISCOUNT);
        c1.setExpiryDate(new Date(10, 10, 2023));
        Coupon c2 = new Coupon();
        c2.setCode("ABCD12");
        c2.setStoreId(1L);
        c2.setPercentage(10);
        c2.setType(CouponType.DISCOUNT);
        c2.setExpiryDate(new Date(10, 10, 2023));
        Coupon c3 = new Coupon();
        c3.setCode("ABDF12");
        c2.setStoreId(1L);
        c2.setPercentage(10);
        c2.setType(CouponType.DISCOUNT);
        c2.setExpiryDate(new Date(10, 10, 2023));
        return Stream.of(
                Arguments.of(c1, c1, true),
                Arguments.of(c1, c2, true),
                Arguments.of(c2, c3, false),
                Arguments.of(c1, null, false),
                Arguments.of(c1, new Date(10, 10, 2023), false)
        );
    }

    @SneakyThrows
    @Test
    void isValid() {
        coupon.setExpiryDate(new Date(24, 05, 2023));
        assertTrue(coupon.isValid());
    }

    @SneakyThrows
    @Test
    void isValidFalse() {
        coupon.setExpiryDate(new Date(24, 05, 2003));
        assertFalse(coupon.isValid());
    }

    @ParameterizedTest
    @MethodSource("codeFormatCases")
    void validCodeFormat(String input, boolean expected) {
        assertThat(Coupon.validCodeFormat(input)).isEqualTo(expected);
    }

    static Stream<Arguments> codeFormatCases() {
        return Stream.of(
                Arguments.of("ABCD12", true),
                Arguments.of("Abdf67", true),
                Arguments.of("6735AB", false),
                Arguments.of("AB23", false),
                Arguments.of("ABCD?2", false),
                Arguments.of("", false),
                Arguments.of(null, false)
        );
    }

    @Test
    void getCode() {
        assertEquals(c1.getCode(), "ABCD12");
    }

    @SneakyThrows
    @Test
    void getExpiryDate() {
        assertEquals(c1.getExpiryDate(), new Date(10, 10, 2023));
    }

    @Test
    void getStoreId() {
        assertEquals(c1.getStoreId(), 1L);
    }

    @Test
    void getType() {
        assertEquals(c1.getType(), CouponType.DISCOUNT);
    }

    @Test
    void getPercentage() {
        assertEquals(c1.getPercentage(), 10);
    }

}