package net.sharksystem.ui.messenger.cli;

import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;

public class CredentialReceivedListener extends SharkNetMessengerAppListener implements SharkCredentialReceivedListener {
    public CredentialReceivedListener(SharkNetMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public void credentialReceived(CredentialMessage credentialMessage) {
        // credential message received
        sharkMessengerApp.addPendingCredentialMessage(credentialMessage);
    }
}
