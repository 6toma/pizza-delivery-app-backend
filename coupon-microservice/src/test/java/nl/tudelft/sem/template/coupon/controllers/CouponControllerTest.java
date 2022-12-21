package nl.tudelft.sem.template.coupon.controllers;

import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import nl.tudelft.sem.store.domain.StoreOwnerValidModel;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.coupon.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CouponControllerTest {

    private CouponController couponController;
    private CouponRepository repo;
    private AuthManager authManager;
    private RequestHelper requestHelper;
    private Coupon c = new Coupon();

    @SneakyThrows
    @BeforeEach
    void setup() {
        repo = Mockito.mock(CouponRepository.class);
        authManager = Mockito.mock(AuthManager.class);
        requestHelper = Mockito.mock(RequestHelper.class);
        couponController = new CouponController(authManager, requestHelper, repo);
        c.setCode("ABCD12");
        c.setExpiryDate(new Date(10, 10, 2024));
        c.setStoreId(2L);
        c.setType(CouponType.ONE_PLUS_ONE);
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
    void getCouponsForStoreInvalid() {
        long storeId = 1L;
        when(requestHelper.postRequest(8084, "/existsByStoreId", storeId, Boolean.class))
            .thenReturn(false);
        assertThrows(InvalidStoreIdException.class, () -> couponController.getCouponsForStore(storeId));
    }

    @Test
    void getCouponsForStoreEmpty() {
        long storeId = 1L;
        when(requestHelper.postRequest(8084, "/existsByStoreId", storeId, Boolean.class))
            .thenReturn(true);
        when(repo.findByStoreId(storeId)).thenReturn(new ArrayList<>());
        ResponseEntity<List<Coupon>> res = couponController.getCouponsForStore(storeId);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(new ArrayList<>());
    }

    @Test
    void getCouponsForStore() {
        long storeId = 1L;
        Coupon c2 = new Coupon();
        c2.setStoreId(storeId);
        c.setStoreId(storeId);
        when(requestHelper.postRequest(8084, "/existsByStoreId", storeId, Boolean.class))
            .thenReturn(true);
        when(repo.findByStoreId(storeId)).thenReturn(List.of(c, c2));
        ResponseEntity<List<Coupon>> res = couponController.getCouponsForStore(storeId);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(List.of(c, c2));
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
    void addCouponNoStoreId() {
        String code = "ABCD56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        assertThrows(InvalidStoreIdException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponIncomplete() {
        String code = "ABCD56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setStoreId(1L);
        assertThrows(IncompleteCouponException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponIncomplete2() {
        String code = "ABCD56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setStoreId(1L);
        coupon.setType(CouponType.ONE_PLUS_ONE);
        assertThrows(IncompleteCouponException.class, () -> couponController.addCoupon(coupon));
    }

    @SneakyThrows
    @Test
    void addCouponIncomplete3() {
        String code = "ABCD56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setStoreId(1L);
        coupon.setExpiryDate(new Date(10, 10, 2024));
        assertThrows(IncompleteCouponException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponNotRegionalManager() {
        String code = "ABCD56";
        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setStoreId(-1L);
        when(authManager.getRole())
            .thenReturn("ROLE_STORE_OWNER");
        assertThrows(NotRegionalManagerException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponWrongStoreId() {
        when(authManager.getNetId()).thenReturn("netId");
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(authManager.getNetId(), c.getStoreId());
        when(requestHelper.postRequest(8084, "/checkStoreowner", sovm, Boolean.class))
            .thenThrow(new InvalidStoreIdException());
        assertThrows(InvalidStoreIdException.class, () -> couponController.addCoupon(c));
    }

    @Test
    void addCouponWrongPair() {
        when(authManager.getNetId()).thenReturn("netId");
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(authManager.getNetId(), c.getStoreId());
        when(requestHelper.postRequest(8084, "/checkStoreowner", sovm, Boolean.class))
            .thenReturn(false);
        assertThrows(InvalidStoreIdException.class, () -> couponController.addCoupon(c));
    }

    @Test
    void addCouponNormal() {
        when(authManager.getNetId()).thenReturn("netId");
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(authManager.getNetId(), c.getStoreId());
        when(requestHelper.postRequest(8084, "/checkStoreowner", sovm, Boolean.class))
            .thenReturn(true);
        ResponseEntity<Coupon> res = couponController.addCoupon(c);
        verify(repo).save(c);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(c);
    }

    @Test
    void selectCouponEmptyPriceList() {
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new ArrayList<>(), List.of("ABDC12"));
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void selectCouponEmptyCouponList() {
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(List.of(10.0), new ArrayList<>());
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void selectCouponInvalidCoupon() {
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(List.of(10.0), List.of("ABC76"));
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void selectCouponUsedCoupon() {

    }

    @Test
    void selectCouponExpensiveOverUsed() {
        Coupon c2 = new Coupon();
        c2.setType(CouponType.DISCOUNT);
        c2.setPercentage(90);
    }

    @Test
    void selectCouponNormal() {
        
    }

    @Test
    void getAuthManager() {
        assertThat(couponController.getAuthManager()).isEqualTo(authManager);
    }

    @Test
    void getRepo() {
        assertThat(couponController.getRepo()).isEqualTo(repo);
    }

    @Test
    void getRequestHelper() { assertThat(couponController.getRequestHelper()).isEqualTo(requestHelper); }
}