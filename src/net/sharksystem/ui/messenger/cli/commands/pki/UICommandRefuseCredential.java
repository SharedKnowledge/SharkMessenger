package net.sharksystem.ui.messenger.cli.commands.pki;

import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandWithSingleInteger;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;

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