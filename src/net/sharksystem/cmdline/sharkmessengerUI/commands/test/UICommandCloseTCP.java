package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.io.IOException;
import java.util.List;

public class UICommandCloseTCP extends UICommand {
    private final UICommandIntegerArgument portNumber;

    public UICommandCloseTCP(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI, String identifier, boolean rememberCommand) {
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
        sb.append("Closes a specified open port - except no more new connections to be established");
        // append hint for how to use
        return sb.toString();
    }
}
