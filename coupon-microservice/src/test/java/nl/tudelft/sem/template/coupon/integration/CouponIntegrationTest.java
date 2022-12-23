package nl.tudelft.sem.template.coupon.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.models.PricesCodesModel;
import nl.tudelft.sem.template.coupon.domain.Coupon;
import nl.tudelft.sem.template.coupon.domain.CouponRepository;
import nl.tudelft.sem.template.coupon.domain.CouponType;
import nl.tudelft.sem.template.coupon.domain.Date;
import nl.tudelft.sem.template.store.domain.StoreOwnerValidModel;
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class CouponIntegrationTest extends IntegrationTest {

    @Autowired
    private CouponRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        repository.flush();
        Mockito.reset(requestHelper);
    }

    @Test
    @SneakyThrows
    void testCouponByCodeSuccess() {
        Coupon c = new Coupon("ABCD12", new Date(10, 10, 2025), 1L, CouponType.ONE_PLUS_ONE);
        repository.save(c);
        ResultActions res = getCouponByCode("ABCD12");
        res.andExpect(status().isOk());
        Coupon returnedCoupon = parseResponseJson(res, Coupon.class);
        assertThat(c).isEqualTo(returnedCoupon);
    }

    @Test
    @SneakyThrows
    void testCouponByCodeInvalidCodeFormat() {
        ResultActions res = getCouponByCode("ABC76");
        res.andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testCouponByCodeNotExists() {
        ResultActions res = getCouponByCode("ABCD12");
        res.andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void testCouponsForStore() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), 1L, CouponType.ONE_PLUS_ONE);
        Coupon c2 = new Coupon("ABCD34", new Date(10, 10, 2025), 2L, CouponType.ONE_PLUS_ONE);
        Coupon c3 = new Coupon("ABCD14", new Date(10, 10, 2025), -1L, CouponType.ONE_PLUS_ONE);
        repository.save(c1);
        repository.save(c2);
        repository.save(c3);
        when(requestHelper.postRequest(8084, "/store/existsByStoreId", 1L, Boolean.class)).thenReturn(true);
        ResultActions res = getCouponsForStore(1L);
        res.andExpect(status().isOk());
        List<Coupon> fin = Arrays.asList(parseResponseJson(res, Coupon[].class));
        assertThat(fin.contains(c1)).isTrue();
        assertThat(fin.contains(c2)).isFalse();
        assertThat(fin.contains(c3)).isTrue();
        assertThat(fin.size()).isEqualTo(2);
    }

    @SneakyThrows
    @Test
    void testCouponsForStoreNotExists() {
        when(requestHelper.postRequest(8084, "/store/existsByStoreId", 12345L, Boolean.class))
            .thenReturn(false);
        ResultActions result = getCouponsForStore(12345L);
        result.andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void testAddCouponStoreOwner() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), 1L, CouponType.ONE_PLUS_ONE);
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(TEST_USER, 1L);
        when(requestHelper.postRequest(8084, "/store/checkStoreowner", sovm, Boolean.class)).thenReturn(true);
        ResultActions res = addCoupon(c1, UserRole.STORE_OWNER);
        res.andExpect(status().isOk());
        assertThat(repository.count()).isEqualTo(1);
    }

    @SneakyThrows
    @Test
    void testAddCouponRegionalManager() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), -1L, CouponType.ONE_PLUS_ONE);
        ResultActions res = addCoupon(c1, UserRole.REGIONAL_MANAGER);
        res.andExpect(status().isOk());
        assertThat(repository.count()).isEqualTo(1);
    }

    @SneakyThrows
    @Test
    void testAddCouponCodeNull() {
        Coupon c1 = new Coupon(null, new Date(10, 10, 2025), 1L, CouponType.ONE_PLUS_ONE);
        ResultActions res = addCoupon(c1, UserRole.REGIONAL_MANAGER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponCodeInvalid() {
        Coupon c1 = new Coupon("AB12", new Date(10, 10, 2025), 1L, CouponType.DISCOUNT);
        ResultActions res = addCoupon(c1, UserRole.STORE_OWNER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponDiscountPercentageNull() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), 1L, CouponType.DISCOUNT);
        ResultActions res = addCoupon(c1, UserRole.REGIONAL_MANAGER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponInvalidStoreId() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), null, CouponType.DISCOUNT);
        ResultActions res = addCoupon(c1, UserRole.STORE_OWNER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponStoreOwnerNoRegionalManager() {
        Coupon c1 = new Coupon("ABCD", new Date(10, 10, 2025), -1L, CouponType.ONE_PLUS_ONE);
        ResultActions res = addCoupon(c1, UserRole.STORE_OWNER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponNoType() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), 1L, null);
        ResultActions res = addCoupon(c1, UserRole.REGIONAL_MANAGER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponNoExpiryDate() {
        Coupon c1 = new Coupon("ABCD12", null, 1L, CouponType.DISCOUNT);
        ResultActions res = addCoupon(c1, UserRole.REGIONAL_MANAGER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponNotOwnerOfStore() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), 1L, CouponType.ONE_PLUS_ONE);
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(TEST_USER, 1L);
        when(requestHelper.postRequest(8084, "/store/checkStoreowner", sovm, Boolean.class)).thenReturn(false);
        ResultActions res = addCoupon(c1, UserRole.STORE_OWNER);
        res.andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }

    @SneakyThrows
    @Test
    void testAddCouponAlreadyExists() {
        when(requestHelper.postRequest(anyInt(), any(), any(), any())).thenReturn(true);
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), -1L, CouponType.ONE_PLUS_ONE);
        Coupon c2 = new Coupon("ABCD12", new Date(11, 10, 2025), -1L, CouponType.DISCOUNT, 20);
        addCoupon(c1, UserRole.REGIONAL_MANAGER).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void testSelectCouponDiscount() {
        Coupon c1 = repository.save(new Coupon("ABCD12", new Date(10, 10, 2025), -1L, CouponType.ONE_PLUS_ONE));
        Coupon c2 = repository.save(new Coupon("ABCD13", new Date(11, 10, 2025), -1L, CouponType.DISCOUNT, 90));
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, List.of(20.0, 20.0), List.of("ABCD12", "ABCD13"));
        mockCheckUsedCoupons(pcm, pcm.getCodes());
        ResultActions res = selectCoupon(pcm).andExpect(status().isOk());
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isEqualTo("ABCD13");
        assertThat(model.getPrice()).isEqualTo(4.0);
    }

    @SneakyThrows
    @Test
    void testSelectCouponOnePlusOne() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), -1L, CouponType.ONE_PLUS_ONE);
        Coupon c2 = new Coupon("ABCD34", new Date(11, 10, 2025), -1L, CouponType.DISCOUNT, 5);
        repository.save(c1);
        repository.save(c2);
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, List.of(10.0, 20.0), List.of("ABCD12", "ABCD34"));
        mockCheckUsedCoupons(pcm, pcm.getCodes());
        ResultActions res = selectCoupon(pcm);
        res.andExpect(status().isOk());
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isEqualTo("ABCD12");
        assertThat(model.getPrice()).isEqualTo(20.0);
    }

    @SneakyThrows
    @Test
    void testSelectCouponPricesNull() {
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, null, List.of("ABCD12"));
        ResultActions res = selectCoupon(pcm);
        res.andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void testSelectCouponPricesEmpty() {
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, Collections.emptyList(), List.of("ABCD12"));
        ResultActions res = selectCoupon(pcm);
        res.andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void testSelectCouponUnusedCodesNull() {
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, List.of(20.0), List.of("ABCD12"));
        mockCheckUsedCoupons(null);
        ResultActions res = selectCoupon(pcm);
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isNull();
        assertThat(model.getPrice()).isEqualTo(20.0);
    }

    @SneakyThrows
    @Test
    void testSelectCouponUnusedCodesEmpty() {
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, List.of(20.0), List.of("ABCD12"));
        mockCheckUsedCoupons(Arrays.asList());
        var res = selectCoupon(pcm).andExpect(status().isOk());
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isNull();
        assertThat(model.getPrice()).isEqualTo(20.0);
    }

    @SneakyThrows
    @Test
    void testSelectCouponInvalidCode() {
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, List.of(10.0), List.of("ABC12"));
        mockCheckUsedCoupons(pcm.getCodes());
        ResultActions res = selectCoupon(pcm).andExpect(status().isOk());
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isNull();
        assertThat(model.getPrice()).isEqualTo(10.0);
    }

    @SneakyThrows
    @Test
    void testSelectCouponDoesNotExist()  {
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, List.of(20.0), List.of("ABCD12"));
        mockCheckUsedCoupons(List.of("ABCD12"));
        ResultActions res = selectCoupon(pcm);
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isNull();
        assertThat(model.getPrice()).isEqualTo(20.0);
    }

    @SneakyThrows
    @Test
    void testSelectCouponStoreIdDoesNotMatch() {
        Coupon c1 = new Coupon("ABCD12", new Date(10, 10, 2025), 1L, CouponType.ONE_PLUS_ONE);
        repository.save(c1);
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 2L, List.of(20.0), List.of("ABCD12"));
        mockCheckUsedCoupons(List.of("ABCD12"));
        ResultActions res = selectCoupon(pcm);
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isNull();
        assertThat(model.getPrice()).isEqualTo(20.0);
    }

    @SneakyThrows
    @Test
    void testSelectCouponAllInvalid() {
        PricesCodesModel pcm = new PricesCodesModel(TEST_USER, 1L, List.of(10.0, 10.0), List.of("ABC12", "DEF23", "GHI45"));
        mockCheckUsedCoupons(pcm.getCodes());
        ResultActions res = selectCoupon(pcm).andExpect(status().isOk());
        CouponFinalPriceModel model = parseResponseJson(res, CouponFinalPriceModel.class);
        assertThat(model.getCode()).isNull();
        assertThat(model.getPrice()).isEqualTo(20.0);
    }

    private void mockCheckUsedCoupons(PricesCodesModel model, List<String> response) {
        when(requestHelper
            .postRequest(8081, "/customers/checkUsedCoupons/" + model.getNetId(), model.getCodes(), List.class))
            .thenReturn(response);
    }

    private void mockCheckUsedCoupons(List<String> response) {
        when(requestHelper.postRequest(eq(8081), eq("/customers/checkUsedCoupons/" + TEST_USER), any(), eq(List.class)))
            .thenReturn(response);
    }

    @SneakyThrows
    private ResultActions getCouponByCode(String code) {
        return mockMvc.perform(authenticated(get("/coupon")).content(code));
    }

    @SneakyThrows
    private ResultActions getCouponsForStore(long storeId) {
        return mockMvc.perform(authenticated(get("/getCouponsForStore"), UserRole.REGIONAL_MANAGER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.valueOf(storeId)));
    }

    @SneakyThrows
    private ResultActions addCoupon(Coupon coupon, UserRole role) {
        return mockMvc.perform(authenticated(post("/addCoupon"), role)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(coupon)));
    }

    @SneakyThrows
    private ResultActions selectCoupon(PricesCodesModel pcm) {
        return mockMvc.perform(microservice(post("/selectCoupon"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(pcm)));
    }

}
