package nl.tudelft.sem.template.coupon.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DiscountCouponIncompleteException extends RuntimeException {

    static final long serialVersionUID = -2387516998124529948L;

    /**
     * Exception thrown when discount percentage has not been determined by supplier.
     */
    public DiscountCouponIncompleteException() {
        super();
    }
}
