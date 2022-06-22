package net.sharksystem.messenger;

public interface SharkMessagesReceivedListener {
    /**
     * New messages arrived
     * @param uri channel uri
     */
    void sharkMessagesReceived(CharSequence uri);
}
