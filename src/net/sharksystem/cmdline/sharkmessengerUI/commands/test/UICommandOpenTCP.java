package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.io.IOException;
import java.util.List;

public class UICommandOpenTCP extends UICommand {

    private final UICommandIntegerArgument portNumber;

    public UICommandOpenTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.portNumber = new UICommandIntegerArgument(sharkMessengerApp);
    }

    /**
     * Extracts the first parameter as portNumber if parseable
     * @param arguments 0: portNumber.
     * @return true if argument is parsable (and valide?)
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.isEmpty()) {
            return false;
        }
        boolean isParsable = this.portNumber.tryParse(arguments.get(0));
        // maybe checks not necessary ... just wait for IO Exception from SocketFactory
        // check if in range of valide portnumbers (figure out not reserved numbers)
        if (this.portNumber.getValue() < 1 || this.portNumber.getValue() >= 65535) {
            this.printErrorMessage("Argument is not a valid port number");
            return false;
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
