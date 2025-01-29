package net.sharksystem.app.messenger;

import java.io.IOException;

public interface SharkNetMessageList {
    SharkNetMessage getSharkMessage(int position, boolean chronologically) throws SharkNetMessengerException;

    int size() throws IOException;
}
