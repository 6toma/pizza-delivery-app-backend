package nl.tudelft.sem.template.example.domain;

public class CouponDoesNotExistException extends RuntimeException {

    static final long serialVersionUID = -2387516998124229948L;

    public CouponDoesNotExistException(String code) {
        super(code);
    }
}
