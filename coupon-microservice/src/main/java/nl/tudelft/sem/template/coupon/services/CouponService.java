package nl.tudelft.sem.template.coupon.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.coupon.domain.Coupon;
import nl.tudelft.sem.template.coupon.domain.CouponType;
import nl.tudelft.sem.template.coupon.domain.Date;

public class CouponService {

    public static double applyDiscount(Coupon coupon, List<Double> prices) {
        if(coupon.getType() != CouponType.DISCOUNT)
            return -1;
        return prices.stream().mapToDouble(Double::doubleValue).sum()
            * (100 - coupon.getPercentage()) / 100;
    }

    public static double applyOnePlusOne(Coupon coupon, List<Double> prices) {
        if(coupon.getType() != CouponType.ONE_PLUS_ONE)
            return -1;
        List<Double> copy = new ArrayList<>(prices);
        Collections.sort(copy);
        copy.remove(0);
        return copy.stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * Method isValid checks whether a coupon is eligible.
     * A coupon is eligible if the expiry date has not yet been met.
     *
     * @return boolean
     */
    public static boolean isValid(Clock clock, Date expiryDate) {
        LocalDate currDate = LocalDate.now(clock);
        LocalDate ed = LocalDate.of(expiryDate.getYear(), expiryDate.getMonth(), expiryDate.getDay());
        return currDate.isBefore(ed);
    }

    /**
     * Checks if the code format is valid.
     * A code is valid if it is composed by 4 letters followed by 2 digits.
     *
     * @return boolean
     */
    public static boolean validCodeFormat(String code) {
        return code.length() == 6 && code.substring(0, 4).chars().allMatch(Character::isLetter)
            && code.substring(4).chars().allMatch(Character::isDigit);
    }

}
