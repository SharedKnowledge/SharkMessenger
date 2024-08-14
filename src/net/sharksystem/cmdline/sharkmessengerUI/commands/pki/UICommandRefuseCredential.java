package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.*;

public class UICommandRefuseCredential extends AbstractCommandWithIndex {
    public UICommandRefuseCredential(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                     String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        this.getSharkMessengerApp().refusePendingCredentialMessageOnIndex(this.getIndex());
    }

    @Override
    public String getDescription() {
        return "refuse pending credential message";
    }
}