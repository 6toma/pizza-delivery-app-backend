package nl.tudelft.sem.template.coupon.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
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
