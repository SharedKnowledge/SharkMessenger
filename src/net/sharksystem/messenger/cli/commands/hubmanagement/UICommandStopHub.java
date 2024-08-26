package net.sharksystem.messenger.cli.commands.hubmanagement;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commands.helper.AbstractCommandWithSingleInteger;

import java.io.IOException;

public class UICommandStopHub extends AbstractCommandWithSingleInteger {
    public UICommandStopHub(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        try {
            this.getSharkMessengerApp().stopHub(this.getIntegerArgument());
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