package net.sharksystem.ui.messenger.cli.commands.hubmanagement;

import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandWithSingleInteger;

import java.io.IOException;
import java.util.List;

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

    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            this.getSharkMessengerApp().tellUIError("portnumber missing");
            return false;
        }
        return super.handleArguments(arguments);
    }
}