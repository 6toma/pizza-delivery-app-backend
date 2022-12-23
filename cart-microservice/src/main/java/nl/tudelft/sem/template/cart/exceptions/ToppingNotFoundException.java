package nl.tudelft.sem.template.cart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to indicate the NetID is already in use.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Topping not found")
public class ToppingNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public ToppingNotFoundException(String topping) {
        super(topping);
    }
}
