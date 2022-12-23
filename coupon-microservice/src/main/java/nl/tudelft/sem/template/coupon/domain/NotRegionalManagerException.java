package nl.tudelft.sem.template.coupon.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotRegionalManagerException extends RuntimeException {

    static final long serialVersionUID = -2387516998124229567L;

    public NotRegionalManagerException() {
        super();
    }
}
