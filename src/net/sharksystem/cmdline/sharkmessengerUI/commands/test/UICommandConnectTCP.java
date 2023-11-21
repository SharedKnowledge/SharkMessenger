package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.io.IOException;
import java.util.List;

public class UICommandConnectTCP extends UICommand {

    private final UICommandIntegerArgument portNumber;

    private final UICommandStringArgument hostName;

    public UICommandConnectTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.portNumber = new UICommandIntegerArgument(sharkMessengerApp);
        this.hostName = new UICommandStringArgument(sharkMessengerApp);
    }

    /**
     * Extracts first and second parameters as portNumber and hostName
     * @param arguments 0:portNumber 1:hostName
     * @return true if arguments are parsable (and valide?)
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 2) {
            return false;
        }
        boolean isParsable = this.portNumber.tryParse(arguments.get(0))
                && this.hostName.tryParse(arguments.get(1));
        // maybe checks not necessary ... just wait for IO Exception from SocketFactory
        // check if in range of valide portnumbers
        if (this.portNumber.getValue() < 1 || this.portNumber.getValue() >= 65535) {
            this.printErrorMessage("Argument is not a valid port number");
            return false;
        }
        // check for valide hostname ?
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
            this.getSharkMessengerApp().connectOverTCP(this.hostName.getValue(), this.portNumber.getValue());
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
