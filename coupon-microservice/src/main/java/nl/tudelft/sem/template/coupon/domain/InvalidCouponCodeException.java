package nl.tudelft.sem.template.coupon.domain;

public class InvalidCouponCodeException extends RuntimeException {

    static final long serialVersionUID = -2387516998124229948L;

    /**
     * Exception thrown when coupon code format is incorrect.
     *
     * @param message , the code passed
     */
    public InvalidCouponCodeException(String message) {
        super(message);
    }
}
