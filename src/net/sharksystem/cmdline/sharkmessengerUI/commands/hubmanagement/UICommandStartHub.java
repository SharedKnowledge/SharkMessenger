package net.sharksystem.cmdline.sharkmessengerUI.commands.hubmanagement;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleInteger;

import java.io.IOException;

public class UICommandStartHub extends AbstractCommandWithSingleInteger {
    public UICommandStartHub(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        try {
            this.getSharkMessengerApp().startHub(this.getIntegerArgument());
        } catch (IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Open new port for establishing TCP connections with.");
        // append hint for how to use
        return sb.toString();
    }
}