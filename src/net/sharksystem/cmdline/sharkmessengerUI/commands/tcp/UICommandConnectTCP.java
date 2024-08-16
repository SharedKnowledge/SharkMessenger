package net.sharksystem.cmdline.sharkmessengerUI.commands.tcp;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandStringArgument;

import java.io.IOException;
import java.util.List;

/**
 * This command conects to a peer over TCP/IP.
 */
public class UICommandConnectTCP extends UICommand {

    private final UICommandIntegerArgument portNumber;

    private final UICommandStringArgument hostName;

    public UICommandConnectTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.portNumber = new UICommandIntegerArgument(sharkMessengerApp);
        this.hostName = new UICommandStringArgument(sharkMessengerApp);
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>port - int</li>
     *  <li>host - String</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 2) {
            System.err.println("host(ip/name) and port number required");
            return false;
        }

        boolean isParsable = this.hostName.tryParse(arguments.get(0))
                && this.portNumber.tryParse(arguments.get(1));

        if(!isParsable) {
            System.err.println("failed to parse hostname and port number: "
                    + arguments.get(0) + " | " + arguments.get(1));
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
            this.getSharkMessengerApp().connectTCP(this.hostName.getValue(), this.portNumber.getValue());
        } catch (IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Connect to a host on given port number.");
        // append hint for how to use
        return sb.toString();
    }
}
