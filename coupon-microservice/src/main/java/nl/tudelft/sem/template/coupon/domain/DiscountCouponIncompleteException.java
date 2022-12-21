package nl.tudelft.sem.template.coupon.domain;

public class DiscountCouponIncompleteException extends RuntimeException {

    static final long serialVersionUID = -2387516998124229948L;

    /**
     * Exception thrown when discount percentage has not been determined by supplier.
     */
    public DiscountCouponIncompleteException() {
        super();
    }
}
