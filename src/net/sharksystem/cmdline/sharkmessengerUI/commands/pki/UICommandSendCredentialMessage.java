package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleString;

public class UICommandSendCredentialMessage extends AbstractCommandWithSingleString {
    private static final String NO_PEER_ID = null;

    public UICommandSendCredentialMessage(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                          String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand, true, NO_PEER_ID);
    }

    @Override
    protected void execute() throws Exception {
        String peerID = this.getStringArgument();
        if(peerID == NO_PEER_ID) {
            // send credentials to all
            this.getSharkMessengerApp().tellUIError("TODO: implement version and send credentials in all open connections");
        } else {
            this.getSharkMessengerApp().tellUI("going to send credential message to peer " + peerID);
            this.getSharkMessengerApp().getSharkPKIComponent().sendOnlineCredentialMessage(peerID);
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Send a credential message to a specific peer to ask for a certificate.");
        return sb.toString();
    }
}
