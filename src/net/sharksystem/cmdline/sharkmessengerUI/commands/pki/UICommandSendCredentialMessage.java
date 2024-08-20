package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleString;

public class UICommandSendCredentialMessage extends AbstractCommandWithSingleString {
    private static final String NO_PEER_NAME = null;

    public UICommandSendCredentialMessage(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                          String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand, true, NO_PEER_NAME);
    }

    @Override
    protected void execute() throws Exception {
        String peerName = this.getStringArgument();
        if(peerName == NO_PEER_NAME) {
            // send credentials to all
            this.getSharkMessengerApp().tellUI("going to send credential message to anybody");
            this.getSharkMessengerApp().getSharkPKIComponent().sendTransientCredentialMessage();
        } else {
            this.getSharkMessengerApp().tellUI("won't work since we need an id instead of a name");
            this.getSharkMessengerApp().tellUI("going to send credential message to peer " + peerName);
            this.getSharkMessengerApp().getSharkPKIComponent().sendTransientCredentialMessage(peerName);
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Send a credential message to a specific peer to ask for a certificate.");
        return sb.toString();
    }
}
