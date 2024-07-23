package event.method.exception;

import java.io.Serial;

/**
 * На случай неудачи создания посредника.
 */
public final class SubscriberGenerationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4428641452893128435L;

    public SubscriberGenerationException(String message) {
        super(message);
    }

    public SubscriberGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}