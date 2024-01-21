package io.inbank.loan.common.exception;

public class InbankException extends RuntimeException {

    protected InbankException() {
        super();
    }

    public InbankException(String message) {
        super(message);
    }

    public InbankException(String message, Throwable cause) {
        super(message, cause);
    }
}
