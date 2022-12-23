package nl.tudelft.sem.template.coupon.controllers;

import java.util.List;
import java.util.PriorityQueue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.annotations.role.MicroServiceInteraction;
import nl.tudelft.sem.template.authentication.annotations.role.RoleStoreOwnerOrRegionalManager;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.models.PricesCodesModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.coupon.domain.Coupon;
import nl.tudelft.sem.template.coupon.domain.CouponRepository;
import nl.tudelft.sem.template.coupon.domain.CouponType;
import nl.tudelft.sem.template.coupon.domain.DiscountCouponIncompleteException;
import nl.tudelft.sem.template.coupon.domain.IncompleteCouponException;
import nl.tudelft.sem.template.coupon.domain.InvalidCouponCodeException;
import nl.tudelft.sem.template.coupon.domain.InvalidStoreIdException;
import nl.tudelft.sem.template.coupon.domain.NotRegionalManagerException;
import nl.tudelft.sem.template.coupon.services.CouponService;
import nl.tudelft.sem.template.store.domain.StoreOwnerValidModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Coupon Controller.
 */
@RestController
@RequiredArgsConstructor
@Data
public class CouponController {

    private static final int ONE = 1;

    private final transient AuthManager authManager;
    private final RequestHelper requestHelper;
    private final CouponRepository repo;
    private final CouponService couponService;

    /**
     * Retrieves coupon using passed code. throws InvalidCouponException if the coupon code format is incorrect.
     *
     * @param code the coupon code provided
     * @return Coupon with given code if exists
     */
    @GetMapping("/coupon")
    public ResponseEntity<Coupon> getCouponByCode(@RequestBody String code) {
        if (!couponService.validCodeFormat(code)) {
            throw new InvalidCouponCodeException(code);
        }
        if (!repo.existsById(code)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(code).get());
    }

    /**
     * Returns a list of coupons linked to a store.
     *
     * @return The list of coupons
     */
    @RoleStoreOwnerOrRegionalManager
    @GetMapping("/getCouponsForStore")
    public ResponseEntity<List<Coupon>> getCouponsForStore(@RequestBody long storeId) {
        if (!requestHelper.postRequest(8084, "/store/existsByStoreId", storeId, Boolean.class)) {
            throw new InvalidStoreIdException();
        }
        return ResponseEntity.ok(repo.findByStoreId(storeId));
    }

    /**
     * Adds a new coupon from the database, storeId depends on person calling (regional manager or store owner).
     *
     * @param coupon the coupon code provided
     * @return Coupon with given code if exists
     * @throws DiscountCouponIncompleteException if discount percentage is missing for a new discount coupon
     */
    @RoleStoreOwnerOrRegionalManager
    @PostMapping("/addCoupon")
    public ResponseEntity<Coupon> addCoupon(@Validated @RequestBody Coupon coupon) {
        if (coupon.getCode() == null) {
            throw new InvalidCouponCodeException("No coupon code provided!");
        }
        if (!couponService.validCodeFormat(coupon.getCode())) {
            throw new InvalidCouponCodeException(coupon.getCode());
        }
        if (coupon.getPercentage() == null && coupon.getType() == CouponType.DISCOUNT) {
            throw new DiscountCouponIncompleteException();
        }
        if (coupon.getStoreId() == null) {
            throw new InvalidStoreIdException();
        }

        if (coupon.getStoreId() == -ONE && authManager.getRole() != UserRole.REGIONAL_MANAGER) {
            throw new NotRegionalManagerException();
        }
        if (coupon.getType() == null || coupon.getExpiryDate() == null) {
            throw new IncompleteCouponException();
        }
        StoreOwnerValidModel sovm = new StoreOwnerValidModel(authManager.getNetId(), coupon.getStoreId());
        if (coupon.getStoreId() == -ONE || requestHelper.postRequest(8084, "/store/checkStoreowner", sovm, Boolean.class)) {
            return ResponseEntity.ok(repo.save(coupon));
        } else {
            throw new InvalidStoreIdException();
        }
    }

    /**
     * Endpoint that takes a list of prices and list of coupons and tries to apply the best coupon.
     *
     * @param pricesCodesModel The model containing all the information about prices and coupons
     * @return The final price after (optionally) applying a coupon
     */
    @MicroServiceInteraction
    @PostMapping("/selectCoupon")
    public ResponseEntity<CouponFinalPriceModel> selectCoupon(@RequestBody PricesCodesModel pricesCodesModel) {
        List<Double> prices = pricesCodesModel.getPrices();
        List<String> codes = pricesCodesModel.getCodes();
        if (prices == null || prices.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        PriorityQueue<CouponFinalPriceModel> pq = new PriorityQueue<>();
        List<String> unusedCodes = requestHelper
            .postRequest(8081, "/customers/checkUsedCoupons/" + pricesCodesModel.getNetId(), codes, List.class);
        if (unusedCodes == null || unusedCodes.isEmpty()) {
            return ResponseEntity.ok(
                new CouponFinalPriceModel(null, prices.stream().mapToDouble(Double::doubleValue).sum()));
        }
        for (String code : unusedCodes) {
            ResponseEntity<Coupon> c;
            try {
                c = getCouponByCode(code);
            } catch (InvalidCouponCodeException e) {
                continue;
            }
            if (c.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                continue;
            }
            Coupon coupon = c.getBody();
            if (coupon.getStoreId() != -ONE && coupon.getStoreId() != pricesCodesModel.getStoreId()) {
                continue;
            }
            if (coupon.getType() == CouponType.DISCOUNT) {
                pq.add(new CouponFinalPriceModel(code, couponService.applyDiscount(coupon, prices)));
            } else {
                if (prices.size() == ONE) {
                    continue;
                }
                pq.add(new CouponFinalPriceModel(code, couponService.applyOnePlusOne(coupon, prices)));
            }
        }
        if (pq.isEmpty()) {
            return ResponseEntity.ok(
                new CouponFinalPriceModel(null, prices.stream().mapToDouble(Double::doubleValue).sum()));
        }
        return ResponseEntity.ok(pq.peek());
    }
}
