package io.pivio;

public class PivioFileNotFoundException extends RuntimeException {

    public PivioFileNotFoundException() {
        super();
    }

    public PivioFileNotFoundException(String s) {
        super(s);
    }

    public PivioFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PivioFileNotFoundException(Throwable cause) {
        super(cause);
    }

}
