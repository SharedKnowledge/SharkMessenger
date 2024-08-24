package net.sharksystem.cmdline.sharkmessengerUI.commands.basics;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleInteger;

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
