package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleInteger;

public class UICommandAcceptCredential extends AbstractCommandWithSingleInteger {
    public UICommandAcceptCredential(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                     String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        this.getSharkMessengerApp().acceptPendingCredentialMessageOnIndex(this.getIntegerArgument());
    }

    @Override
    public String getDescription() {
        return "accept pending credential message";
    }
}