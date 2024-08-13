package net.sharksystem.cmdline.sharkmessengerUI.commands.tcp;

import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.io.IOException;
import java.util.List;

/**
 * This command opens a port for a peer to connect to over TCP/IP.
 */
public class UICommandOpenTCP extends UICommand {

    private final UICommandIntegerArgument portNumber;

    public UICommandOpenTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
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
            System.err.println("port number required");
            return false;
        }

        boolean isParsable = this.portNumber.tryParse(arguments.get(0));
        if(!isParsable) {
            System.err.println("failed to parse port number: " + arguments.get(0));
        }
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
            this.getSharkMessengerApp().openTCPConnection(this.portNumber.getValue());
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
