package nl.tudelft.sem.template.coupon.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStoreIdException extends RuntimeException {

    static final long serialVersionUID = -2387675998124228848L;

    public InvalidStoreIdException() {
        super();
    }
}
