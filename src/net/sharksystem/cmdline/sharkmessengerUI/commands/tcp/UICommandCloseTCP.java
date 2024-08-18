package net.sharksystem.cmdline.sharkmessengerUI.commands.tcp;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleInteger;

import java.io.IOException;
import java.util.List;

/**
 * Close a TCP connection to another peer.
 */
public class UICommandCloseTCP extends AbstractCommandWithSingleInteger {
    public UICommandCloseTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        try {
            this.getSharkMessengerApp().closeTCPConnection(this.getIntegerArgument());
        } catch (IOException e) {
            // exception is expected - ignore it
            //this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Closes a specified open port - except no more new connections to be established.");
        // append hint for how to use
        return sb.toString();
    }
}
