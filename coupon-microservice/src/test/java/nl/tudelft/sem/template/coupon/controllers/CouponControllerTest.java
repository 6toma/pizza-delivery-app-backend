package nl.tudelft.sem.template.coupon.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import nl.tudelft.sem.template.store.domain.StoreOwnerValidModel;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.models.PricesCodesModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.coupon.domain.Coupon;
import nl.tudelft.sem.template.coupon.domain.CouponRepository;
import nl.tudelft.sem.template.coupon.domain.CouponType;
import nl.tudelft.sem.template.coupon.domain.Date;
import nl.tudelft.sem.template.coupon.domain.DiscountCouponIncompleteException;
import nl.tudelft.sem.template.coupon.domain.IncompleteCouponException;
import nl.tudelft.sem.template.coupon.domain.InvalidCouponCodeException;
import nl.tudelft.sem.template.coupon.domain.InvalidStoreIdException;
import nl.tudelft.sem.template.coupon.domain.NotRegionalManagerException;
import nl.tudelft.sem.template.coupon.services.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CouponControllerTest {

    private CouponController couponController;
    private CouponRepository repo;
    private AuthManager authManager;
    private RequestHelper requestHelper;
    private CouponService couponService;
    private Coupon c = new Coupon();

    @Mock
    private Clock clock;
    private Clock fixedClock;

    @SneakyThrows
    @BeforeEach
    void setup() {
        repo = Mockito.mock(CouponRepository.class);
        authManager = Mockito.mock(AuthManager.class);
        requestHelper = Mockito.mock(RequestHelper.class);

        MockitoAnnotations.openMocks(this);

        LocalDate LOCAL_DATE = LocalDate.of(2022, 12, 13);
        fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        couponService = new CouponService(fixedClock);
        couponController = new CouponController(authManager, requestHelper, repo, couponService);
        c.setCode("ABCD12");
        c.setExpiryDate(new Date(10, 10, 2024));
        c.setStoreId(1L);
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
        when(requestHelper.postRequest(8084, "/store/existsByStoreId", storeId, Boolean.class))
            .thenReturn(false);
        assertThrows(InvalidStoreIdException.class, () -> couponController.getCouponsForStore(storeId));
    }

    @Test
    void getCouponsForStoreEmpty() {
        long storeId = 1L;
        when(requestHelper.postRequest(8084, "/store/existsByStoreId", storeId, Boolean.class))
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
        when(requestHelper.postRequest(8084, "/store/existsByStoreId", storeId, Boolean.class))
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
        when(authManager.getRoleAuthority())
            .thenReturn("ROLE_STORE_OWNER");
        assertThrows(NotRegionalManagerException.class, () -> couponController.addCoupon(coupon));
    }

    @Test
    void addCouponWrongStoreId() {
        when(authManager.getNetId()).thenReturn("netId");
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(authManager.getNetId(), c.getStoreId());
        when(requestHelper.postRequest(8084, "/store/checkStoreowner", sovm, Boolean.class))
            .thenThrow(new InvalidStoreIdException());
        assertThrows(InvalidStoreIdException.class, () -> couponController.addCoupon(c));
    }

    @Test
    void addCouponWrongPair() {
        when(authManager.getNetId()).thenReturn("netId");
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(authManager.getNetId(), c.getStoreId());
        when(requestHelper.postRequest(8084, "/store/checkStoreowner", sovm, Boolean.class))
            .thenReturn(false);
        assertThrows(InvalidStoreIdException.class, () -> couponController.addCoupon(c));
    }

    @Test
    void addCouponNormal() {
        when(authManager.getNetId()).thenReturn("netId");
        when(repo.save(any())).thenReturn(c);
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(authManager.getNetId(), c.getStoreId());
        when(requestHelper.postRequest(8084, "/store/checkStoreowner", sovm, Boolean.class))
            .thenReturn(true);
        ResponseEntity<Coupon> res = couponController.addCoupon(c);
        verify(repo).save(c);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(c);
    }

    @Test
    void selectCouponEmptyPriceList() {
        ResponseEntity<CouponFinalPriceModel> res =
            couponController.selectCoupon(new PricesCodesModel("netId", 1, new ArrayList<>(), List.of("ABDC12")));
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void selectCouponEmptyCouponList() {
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 1, List.of(10.0), new ArrayList<>()));
        assertThat(res.getBody().getCode()).isNull();
        assertThat(res.getBody().getPrice()).isEqualTo(10.0);
    }

    @Test
    void selectCouponInvalidCoupon() {
        when(requestHelper.postRequest(8081, "/customers/checkUsedCoupons/Tester", List.of("ABC76"), List.class)).thenReturn(List.of("ABC76"));
        when(repo.existsById("ABCD76")).thenReturn(false);
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 1, List.of(10.0), List.of("ABC76")));
        assertThat(res.getBody().getCode()).isNull();
        assertThat(res.getBody().getPrice()).isEqualTo(10.0);
    }

    @Test
    void selectCouponCouponDoesNotExist() {
        when(requestHelper.postRequest(8081, "/customers/checkUsedCoupons/Tester", List.of("ABCD76"), List.class)).thenReturn(List.of("ABCD76"));
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 1, List.of(10.0), List.of("ABCD76")));
        assertThat(res.getBody().getCode()).isNull();
        assertThat(res.getBody().getPrice()).isEqualTo(10.0);
    }

    @Test
    void selectCouponUsedCoupon() {
        when(repo.existsById("ABCD12")).thenReturn(true);
        when(repo.findById("ABCD12")).thenReturn(Optional.ofNullable(c));
        when(requestHelper.postRequest(8081, "/customers/checkUsedCoupons/Tester", List.of("ABCD12"), List.class)).thenReturn(new ArrayList());
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 1, List.of(10.0), List.of("ABCD12")));
        assertThat(res.getBody().getCode()).isNull();
        assertThat(res.getBody().getPrice()).isEqualTo(10.0);
    }

    @Test
    void selectCouponOtherStoreId() {
        when(repo.existsById("ABCD12")).thenReturn(true);
        when(repo.findById("ABCD12")).thenReturn(Optional.ofNullable(c));
        when(requestHelper.postRequest(8081, "/customers/checkUsedCoupons/Tester", List.of("ABCD12"), List.class)).thenReturn(List.of("ABCD12"));
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 2, List.of(10.0), List.of("ABCD12")));
        assertThat(res.getBody().getCode()).isNull();
        assertThat(res.getBody().getPrice()).isEqualTo(10.0);
    }

    @Test
    void selectCouponOPOSinglePrice() {
        when(repo.existsById("ABCD12")).thenReturn(true);
        when(repo.findById("ABCD12")).thenReturn(Optional.ofNullable(c));
        when(requestHelper.postRequest(8081, "/customers/checkUsedCoupons/Tester", List.of("ABCD12"), List.class)).thenReturn(List.of("ABCD12"));
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 1, List.of(10.0), List.of("ABCD12")));
        assertThat(res.getBody().getCode()).isNull();
        assertThat(res.getBody().getPrice()).isEqualTo(10.0);
    }

    @SneakyThrows
    @Test
    void selectCouponExpensiveOverUsed() {
        Coupon c2 = new Coupon();
        c2.setType(CouponType.DISCOUNT);
        c2.setPercentage(90);
        c2.setCode("CHEA12");
        c2.setExpiryDate(new Date(10, 10, 2025));
        c2.setStoreId(1L);
        when(repo.existsById("CHEA12")).thenReturn(true);
        when(repo.findById("CHEA12")).thenReturn(Optional.ofNullable(c2));
        when(repo.existsById("ABCD12")).thenReturn(true);
        when(repo.findById("ABCD12")).thenReturn(Optional.ofNullable(c));
        when(requestHelper.postRequest(8081, "/customers/checkUsedCoupons/Tester", List.of("ABCD12", "CHEA12"), List.class)).thenReturn(List.of("ABCD12"));
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 1, List.of(10.0, 9.0), List.of("ABCD12", "CHEA12")));
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getCode()).isEqualTo("ABCD12");
    }

    @SneakyThrows
    @Test
    void selectCouponNormal() {
        Coupon c2 = new Coupon();
        c2.setType(CouponType.DISCOUNT);
        c2.setPercentage(90);
        c2.setCode("CHEA12");
        c2.setExpiryDate(new Date(10, 10, 2025));
        c2.setStoreId(1L);
        when(authManager.getNetId()).thenReturn("Tester");
        when(repo.existsById("CHEA12")).thenReturn(true);
        when(repo.findById("CHEA12")).thenReturn(Optional.ofNullable(c2));
        when(repo.existsById("ABCD12")).thenReturn(true);
        when(repo.findById("ABCD12")).thenReturn(Optional.ofNullable(c));
        when(requestHelper.postRequest(8081, "/customers/checkUsedCoupons/Tester", List.of("ABCD12", "CHEA12"), List.class)).thenReturn(List.of("ABCD12", "CHEA12"));
        ResponseEntity<CouponFinalPriceModel> res = couponController.selectCoupon(new PricesCodesModel("Tester", 1, List.of(10.0, 9.0), List.of("ABCD12", "CHEA12")));
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getCode()).isEqualTo("CHEA12");
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
    void getRequestHelper() {
        assertThat(couponController.getRequestHelper()).isEqualTo(requestHelper);
    }
}