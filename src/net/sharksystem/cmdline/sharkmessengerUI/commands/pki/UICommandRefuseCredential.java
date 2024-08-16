package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleInteger;

public class UICommandRefuseCredential extends AbstractCommandWithSingleInteger {
    public UICommandRefuseCredential(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                     String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        this.getSharkMessengerApp().refusePendingCredentialMessageOnIndex(this.getIntegerArgument());
    }

    @Override
    public String getDescription() {
        return "refuse pending credential message";
    }
}