package nl.tudelft.sem.template.authentication;

public class EmailNotValidException extends RuntimeException {

    private static final long serialVersionUID = -12934871;

    public EmailNotValidException(String s) {
        super(s);
    }
}
