package uk.gov.companieshouse.email_producer;

public class EmailSendingException extends RuntimeException {
    
    private static final long serialVersionUID = 206523116407987653L;

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
