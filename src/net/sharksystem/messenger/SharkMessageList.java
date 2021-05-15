package net.sharksystem.messenger;

import net.sharksystem.SharkNotSupportedException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPStorage;

import java.io.IOException;

public interface SharkMessageList {
    SharkMessage getSharkMessage(int position, boolean chronologically) throws SharkMessengerException;

    int size() throws IOException;
}
