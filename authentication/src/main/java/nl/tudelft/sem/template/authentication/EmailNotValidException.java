package nl.tudelft.sem.template.authentication;

/**
 * Exception to indicate the email is already in use.
 */
public class EmailNotValidException extends RuntimeException {
    static final long serialVersionUID = -3327226993124229948L;

    public EmailNotValidException(String error) {
        super(error);
    }
}
