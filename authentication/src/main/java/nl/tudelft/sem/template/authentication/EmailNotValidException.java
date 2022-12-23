package nl.tudelft.sem.template.authentication;

public class EmailNotValidException extends RuntimeException {
    public EmailNotValidException(String s) {
        super(s);
    }
}
