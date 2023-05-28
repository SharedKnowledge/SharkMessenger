package net.sharksystem.cmdline.sharkmessengerUI;

import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;

public class CredentialReceivedListener extends SharkMessengerAppListener implements SharkCredentialReceivedListener {
    public CredentialReceivedListener(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public void credentialReceived(CredentialMessage credentialMessage) {
        // TODO
    }
}
