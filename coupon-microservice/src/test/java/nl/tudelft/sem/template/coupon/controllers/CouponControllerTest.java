package nl.tudelft.sem.template.coupon.controllers;

import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.coupon.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import static org.mockito.Mockito.when;

class CouponControllerTest {

    private CouponController couponController;
    private CouponRepository repo;
    private AuthManager authManager;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(CouponRepository.class);
        authManager = Mockito.mock(AuthManager.class);
        couponController = new CouponController(authManager, repo);
    }

    @Test
    void getCouponByCodeInvalid() {
        String code = "AB56";
        assertThrows(InvalidCouponCodeException.class, () -> couponController.getCouponByCode(code));
    }

    @Test
    void getCouponByCodeBadRequest() {
        String code = "ABCD56";
        when(repo.findById(code)).thenReturn(Optional.empty());
        assertThat(couponController.getCouponByCode(code).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getCouponByCode() {
        String code = "ABCD56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        when(repo.existsById(code)).thenReturn(true);
        when(repo.findById(code)).thenReturn(Optional.of(coupon));
        ResponseEntity<Coupon> res = couponController.getCouponByCode(code);
        assertThat(res.getBody()).isEqualTo(coupon);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getCouponsForStore() {

    }

    @Test
    void addCouponNullCode() {
        Coupon coupon = new Coupon();
        assertThrows(InvalidCouponCodeException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponInvalid() {
        String code = "AB56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        assertThrows(InvalidCouponCodeException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponNoPercentage() {
        String code = "ABCD56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setType(CouponType.DISCOUNT);
        assertThrows(DiscountCouponIncompleteException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponBadRequest() {

    }

    @Test
    void addCouponNormal() {

    }

    @Test
    void getRoles() {
    }

    @Test
    void authorRole() {
    }

    @Test
    void getAuthManager() {
        assertThat(couponController.getAuthManager()).isEqualTo(authManager);
    }

    @Test
    void getRepo() {
        assertThat(couponController.getRepo()).isEqualTo(repo);
    }
}