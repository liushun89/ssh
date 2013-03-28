package net.systemtrap.ssh;

public class CipherDecodingException extends RuntimeException {

    private static final long serialVersionUID = 738938007771269808L;

    public CipherDecodingException(final String message,
            final Throwable cause) {
        super(message, cause, false, false);
    }
}
