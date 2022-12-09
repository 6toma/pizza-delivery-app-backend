package nl.tudelft.sem.template.cart.exceptions;

/**
 * Exception to indicate the NetID is already in use.
 */
public class PizzaNameNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public PizzaNameNotFoundException(String pizzaName) {
        super(pizzaName);
    }
}
