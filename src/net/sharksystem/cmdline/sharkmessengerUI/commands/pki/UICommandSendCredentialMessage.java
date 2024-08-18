package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleString;

public class UICommandSendCredentialMessage extends AbstractCommandWithSingleString {
    public UICommandSendCredentialMessage(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                          String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        String peerID = this.getStringArgument();
        this.getSharkMessengerApp().tellUI("going to send credential message to peer " + peerID);
        this.getSharkMessengerApp().getSharkPKIComponent().sendOnlineCredentialMessage(peerID);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Send a credential message to a specific peer to ask for a certificate.");
        return sb.toString();
    }
}
