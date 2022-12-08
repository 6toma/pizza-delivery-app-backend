package nl.tudelft.sem.template.commons.exceptions;

/**
 * Exception to indicate the NetID is already in use.
 */
public class ToppingAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public ToppingAlreadyInUseException(String topping) {
        super(topping);
    }
}
