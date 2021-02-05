package net.sharksystem.messenger;

import java.util.Collection;

public interface SharkMessagesReceivedListener {
    /**
     * New messages arrived
     * @param uri channel uri
     */
    void sharkMessagesReceived(CharSequence uri);
}
