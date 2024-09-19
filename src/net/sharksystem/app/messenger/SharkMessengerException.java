package net.sharksystem.app.messenger;

import net.sharksystem.SharkException;

public class SharkMessengerException extends SharkException {
    public SharkMessengerException() {
        super();
    }
    public SharkMessengerException(String message) {
        super(message);
    }
    public SharkMessengerException(String message, Throwable cause) {
        super(message, cause);
    }
    public SharkMessengerException(Throwable cause) {
        super(cause);
    }
}
