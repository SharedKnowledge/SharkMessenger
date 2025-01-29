package net.sharksystem.app.messenger;

import net.sharksystem.SharkException;

public class SharkNetMessengerException extends SharkException {
    public SharkNetMessengerException() {
        super();
    }
    public SharkNetMessengerException(String message) {
        super(message);
    }
    public SharkNetMessengerException(String message, Throwable cause) {
        super(message, cause);
    }
    public SharkNetMessengerException(Throwable cause) {
        super(cause);
    }
}
