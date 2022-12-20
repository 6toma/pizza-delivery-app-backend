package nl.tudelft.sem.template.cart.exceptions;

/**
 * Exception to indicate the email is already in use.
 */
public class ToppingAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public ToppingAlreadyInUseException(String topping) {
        super(topping);
    }
}
