package net.sharksystem.utils.cmdline;

import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;
import net.sharksystem.utils.cmdline.model.CLIModel;

public class CredentialReceivedListener extends SharkMessengerAppListener implements SharkCredentialReceivedListener {
    public CredentialReceivedListener(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public void credentialReceived(CredentialMessage credentialMessage) {
        // TODO
    }
}
