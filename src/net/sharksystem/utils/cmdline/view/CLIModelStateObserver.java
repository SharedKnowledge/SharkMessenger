package net.sharksystem.utils.cmdline.view;

import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.pki.CredentialMessage;

/**
 * Interface implemented by observers which want to be informed by the CLIModel when it was changed.
 */
public interface CLIModelStateObserver {

    /**
     * The state of the model changed to "started".
     */
    void started();

    /**
     * The state of the model changed to "terminated".
     */
    void terminated();

    /**
     * Asks the user for his username.
     * @return The username that was entered by the user.
     */
    String getUsername();

    /**
     * Displays a credential message to the user.
     * @param credentialMessage The credential message.
     */
    void displayCredentialMessage(CredentialMessage credentialMessage);

    /**
     * Called when a message was received on a specific channel, but the channel couldn't be found.
     * The channel has probably been removed.
     * @param channelUri The URI of the channel that may have been removed.
     */
    void onChannelDisappeared(String channelUri);

    /**
     * Called when new messages were received.
     * @param messages All messages from the channel in which new messages have been received.
     */
    void displayMessages(SharkMessageList messages);
}
