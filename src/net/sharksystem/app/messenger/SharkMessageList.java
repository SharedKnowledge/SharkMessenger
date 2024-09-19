package net.sharksystem.app.messenger;

import java.io.IOException;

public interface SharkMessageList {
    SharkMessage getSharkMessage(int position, boolean chronologically) throws SharkMessengerException;

    int size() throws IOException;
}
