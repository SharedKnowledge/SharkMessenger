package net.sharksystem.messenger.cli.commands.basics;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commands.helper.AbstractCommandWithSingleString;

public class UICommandEcho extends AbstractCommandWithSingleString {
    public UICommandEcho(SharkMessengerApp sharkMessengerApp, SharkMessengerUI smUI, String echo, boolean b) {
        super(sharkMessengerApp, smUI, echo, b);
    }

    @Override
    protected void execute() throws Exception {
        this.getSharkMessengerApp().tellUI(this.getStringArgument());
    }

    @Override
    public String getDescription() {
        return "echo command";
    }
}
