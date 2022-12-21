package nl.tudelft.sem.template.coupon.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncompleteCouponException extends RuntimeException {

    static final long serialVersionUID = -2386916998124229948L;

    public IncompleteCouponException() {
        super();
    }
}
