package net.sharksystem.messenger.cli.commands.tcp;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commands.helper.AbstractCommandWithSingleInteger;

import java.io.IOException;

/**
 * This command opens a port for a peer to connect to over TCP/IP.
 */
public class UICommandOpenTCP extends AbstractCommandWithSingleInteger {

    public UICommandOpenTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        try {
            this.getSharkMessengerApp().openTCPConnection(this.getIntegerArgument());
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
