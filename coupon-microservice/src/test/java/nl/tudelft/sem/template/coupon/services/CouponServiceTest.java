package nl.tudelft.sem.template.coupon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import nl.tudelft.sem.template.coupon.domain.Coupon;
import nl.tudelft.sem.template.coupon.domain.CouponType;
import nl.tudelft.sem.template.coupon.domain.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CouponServiceTest {

    private Coupon c1;
    private final static LocalDate LOCAL_DATE = LocalDate.of(2022, 12, 13);

    @InjectMocks
    private Coupon coupon;

    @Mock
    private Clock clock;
    private Clock fixedClock;

    private CouponService couponService;

    @SneakyThrows
    @BeforeEach
    void setup() {
        c1 = new Coupon();
        c1.setCode("ABCD12");
        c1.setStoreId(1L);
        c1.setPercentage(10);
        c1.setType(CouponType.DISCOUNT);
        c1.setExpiryDate(new Date(10, 10, 2023));
        MockitoAnnotations.initMocks(this);

        //tell tests to return the specified LOCAL_DATE when calling LocalDate.now(clock)
        fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
        couponService = new CouponService(fixedClock);
    }

    @Test
    void applyDiscount() {
        List<Double> prices = List.of(10.0, 50.0, 20.0, 15.0, 5.0);
        Double finalPrice = 90.0;
        assertEquals(finalPrice, couponService.applyDiscount(c1, prices));
    }

    @Test
    void applyDiscountInvalid() {
        List<Double> prices = List.of(10.0, 50.0, 20.0, 15.0, 5.0);
        c1.setType(CouponType.ONE_PLUS_ONE);
        assertEquals(-1L, couponService.applyDiscount(c1, prices));
    }

    @Test
    void applyOnePlusOneInvalid() {
        List<Double> prices = List.of(10.0, 50.0, 20.0, 15.0, 5.0);
        assertEquals(-1, couponService.applyOnePlusOne(c1, prices));
    }

    @Test
    void applyOnePlusOne() {
        List<Double> prices = List.of(10.0, 50.0, 20.0, 15.0, 5.0);
        Double finalPrice = 95.0;
        c1.setType(CouponType.ONE_PLUS_ONE);
        assertEquals(finalPrice, couponService.applyOnePlusOne(c1, prices));
    }

    @SneakyThrows
    @Test
    void isValid() {
        coupon.setExpiryDate(new Date(24, 05, 2023));
        assertTrue(couponService.isValid(coupon.getExpiryDate()));
    }

    @SneakyThrows
    @Test
    void isValidFalse() {
        coupon.setExpiryDate(new Date(24, 05, 2003));
        assertFalse(couponService.isValid(coupon.getExpiryDate()));
    }

    @ParameterizedTest
    @MethodSource("codeFormatCases")
    void validCodeFormat(String input, boolean expected) {
        assertThat(couponService.validCodeFormat(input)).isEqualTo(expected);
    }

    static Stream<Arguments> codeFormatCases() {
        return Stream.of(
            Arguments.of("ABCD12", true),
            Arguments.of("Abdf67", true),
            Arguments.of("6735AB", false),
            Arguments.of("AB23", false),
            Arguments.of("ABCD?2", false)
        );
    }
}