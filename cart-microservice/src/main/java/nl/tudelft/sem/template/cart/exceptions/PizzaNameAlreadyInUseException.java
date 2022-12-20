package nl.tudelft.sem.template.cart.exceptions;

/**
 * Exception to indicating that pizza name is already in use
 */
public class PizzaNameAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public PizzaNameAlreadyInUseException(String pizzaName) {
        super(pizzaName);
    }
}
