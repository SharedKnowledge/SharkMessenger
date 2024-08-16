package net.sharksystem.cmdline.sharkmessengerUI.commands.tcp;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;

import java.io.IOException;
import java.util.List;

/**
 * Close a TCP connection to another peer.
 */
public class UICommandCloseTCP extends UICommand {
    private final UICommandIntegerArgument portNumber;

    public UICommandCloseTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.portNumber = new UICommandIntegerArgument(sharkMessengerApp);
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>port - int</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            return false;
        }

        boolean isParsable = this.portNumber.tryParse(arguments.get(0));
        return isParsable;
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    protected void execute() throws Exception {
        try {
            this.getSharkMessengerApp().closeTCPConnection(this.portNumber.getValue());
        } catch (IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
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
