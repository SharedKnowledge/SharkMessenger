package net.sharksystem.app.messenger;

public interface SharkMessagesReceivedListener {
    /**
     * New messages arrived
     * @param uri channel uri
     */
    void sharkMessagesReceived(CharSequence uri);
}
