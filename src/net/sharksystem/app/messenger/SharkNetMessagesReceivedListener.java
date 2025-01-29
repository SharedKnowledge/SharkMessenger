package net.sharksystem.app.messenger;

public interface SharkNetMessagesReceivedListener {
    /**
     * New messages arrived
     * @param uri channel uri
     */
    void sharkMessagesReceived(CharSequence uri);
}
