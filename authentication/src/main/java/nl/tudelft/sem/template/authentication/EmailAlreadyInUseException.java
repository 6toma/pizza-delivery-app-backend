package nl.tudelft.sem.template.authentication;

/**
 * Exception to indicate the email is already in use.
 */
public class EmailAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;
    
    public EmailAlreadyInUseException(UserEmail userEmail) {
        super(userEmail.toString());
    }
}
