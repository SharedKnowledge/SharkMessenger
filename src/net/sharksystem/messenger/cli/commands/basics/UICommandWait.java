package net.sharksystem.messenger.cli.commands.basics;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commands.helper.AbstractCommandWithSingleInteger;

public class UICommandWait extends AbstractCommandWithSingleInteger {
    public UICommandWait(SharkMessengerApp sharkMessengerApp, SharkMessengerUI smUI, String wait, boolean b) {
        super(sharkMessengerApp, smUI, wait, b);
    }

    @Override
    protected void execute() throws Exception {
        this.getSharkMessengerApp().tellUI("wait " + this.getIntegerArgument() + " ms ...");
        Thread.sleep(this.getIntegerArgument());
        this.getSharkMessengerApp().tellUI("resume");
    }

    @Override
    public String getDescription() {
        return "waits a milliseconds";
    }
}