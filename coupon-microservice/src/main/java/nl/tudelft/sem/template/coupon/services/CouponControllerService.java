package nl.tudelft.sem.template.coupon.services;

import java.util.List;
import java.util.PriorityQueue;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.commons.models.CouponFinalPriceModel;
import nl.tudelft.sem.template.commons.models.PricesCodesModel;
import nl.tudelft.sem.template.coupon.domain.Coupon;
import nl.tudelft.sem.template.coupon.domain.CouponRepository;
import nl.tudelft.sem.template.coupon.domain.CouponType;
import nl.tudelft.sem.template.coupon.domain.DiscountCouponIncompleteException;
import nl.tudelft.sem.template.coupon.domain.IncompleteCouponException;
import nl.tudelft.sem.template.coupon.domain.InvalidCouponCodeException;
import nl.tudelft.sem.template.coupon.domain.InvalidStoreIdException;
import nl.tudelft.sem.template.coupon.domain.NotRegionalManagerException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CouponControllerService {

    private static final int ONE = 1;

    private CouponService couponService;
    private AuthManager authManager;
    private CouponRepository repo;

    /**
     * Checks that the parameters passed can create a valid coupon.
     *
     * @param coupon
     */
    public void checkValidInput(Coupon coupon) {
        if (!couponService.validCodeFormat(coupon.getCode())) {
            throw new InvalidCouponCodeException(coupon.getCode());
        }
        if (coupon.getStoreId() == -ONE && authManager.getRole() != UserRole.REGIONAL_MANAGER) {
            throw new NotRegionalManagerException();
        }
    }

    /**
     * Checks that all fields required for a coupon to be created are present.
     *
     * @param coupon
     */
    public void checkIncompleteInput(Coupon coupon) {
        if (coupon.getCode() == null) {
            throw new InvalidCouponCodeException("No coupon code provided!");
        }
        if (coupon.getType() == null || coupon.getExpiryDate() == null) {
            throw new IncompleteCouponException();
        }
        if (coupon.getPercentage() == null && coupon.getType() == CouponType.DISCOUNT) {
            throw new DiscountCouponIncompleteException();
        }
        if (coupon.getStoreId() == null) {
            throw new InvalidStoreIdException();
        }
    }

    /**
     * Loops over the possible coupon codes and adds them to a PriorityQueue according to the price they give.
     *
     * @param pricesCodesModel
     * @param prices
     * @param unusedCodes
     * @return PriorityQueue
     */
    public PriorityQueue<CouponFinalPriceModel> buildPriorityQueue(PricesCodesModel pricesCodesModel, List<Double> prices, List<String> unusedCodes) {
        PriorityQueue<CouponFinalPriceModel> pq = new PriorityQueue<>();
        for (String code : unusedCodes) {
            Coupon coupon = checkCouponValid(pricesCodesModel, code);
            if (coupon == null) {
                continue;
            }
            CouponFinalPriceModel cfpm = applyCoupon(prices, code, coupon);
            if (cfpm == null) {
                continue;
            }
            pq.add(cfpm);
        }
        return pq;
    }

    /**
     * Applies coupon accordingly to its type.
     *
     * @param prices
     * @param code
     * @param coupon
     * @return CouponFinalPriceModel
     */
    private CouponFinalPriceModel applyCoupon(List<Double> prices, String code, Coupon coupon) {
        CouponFinalPriceModel cfpm;
        if (coupon.getType() == CouponType.DISCOUNT) {
            cfpm = new CouponFinalPriceModel(code, couponService.applyDiscount(coupon, prices));
        } else {
            if (prices.size() == ONE) {
                return null;
            }
            cfpm = new CouponFinalPriceModel(code, couponService.applyOnePlusOne(coupon, prices));
        }
        return cfpm;
    }

    /**
     * Checks that the coupon has a valid code and exists.
     *
     * @param pricesCodesModel
     * @param code
     * @return
     */
    private Coupon checkCouponValid(PricesCodesModel pricesCodesModel, String code) {
        Coupon c;
        if (!couponService.validCodeFormat(code))
            return null;
        if(!repo.existsById(code))
            return null;
        c = repo.findById(code).get();
        if (c.getStoreId() != -ONE && c.getStoreId() != pricesCodesModel.getStoreId()) {
            return null;
        }
        return c;
    }

}
