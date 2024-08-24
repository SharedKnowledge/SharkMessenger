package net.sharksystem.cmdline.sharkmessengerUI.commands.basics;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleString;

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
