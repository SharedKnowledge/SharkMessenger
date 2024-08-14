package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.*;

public class UICommandAcceptCredential extends AbstractCommandWithIndex {
    public UICommandAcceptCredential(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                     String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        this.getSharkMessengerApp().acceptPendingCredentialMessageOnIndex(this.getIndex());
    }

    @Override
    public String getDescription() {
        return "accept pending credential message";
    }
}