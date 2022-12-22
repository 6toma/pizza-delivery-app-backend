package nl.tudelft.sem.template.checkout.domain;

public class OrderNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public OrderNotFoundException(long orderId) {
        super(String.valueOf(orderId));
    }
}
