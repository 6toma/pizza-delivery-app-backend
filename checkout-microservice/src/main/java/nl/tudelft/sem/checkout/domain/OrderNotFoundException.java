package nl.tudelft.sem.checkout.domain;

public class OrderNotFoundException extends Exception{
    static final long serialVersionUID = -3387516993124229948L;

    public OrderNotFoundException(int orderId) {
        super(String.valueOf(orderId));
    }
}
