package nl.tudelft.sem.template.authentication;

public class EmailNotValidException extends RuntimeException {

    static final long serialVersionUID = -3787516391104229948L;

    public EmailNotValidException(String s) {
        super(s);
    }
}
