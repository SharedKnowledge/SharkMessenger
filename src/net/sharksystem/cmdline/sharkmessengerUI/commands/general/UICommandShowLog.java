package net.sharksystem.cmdline.sharkmessengerUI.commands.general;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;

import java.sql.SQLOutput;
import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

/**
 * Command for displaying the log history to the user.
 * Format: Command followed by parameters (space separated)
 */
public class UICommandShowLog extends UICommand {

    public UICommandShowLog(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute() throws Exception {
        List<String> commandHistory = this.getSharkMessengerUI().getCommandHistory();
        StringBuilder sb = new StringBuilder();
        for (String command : commandHistory) {
            sb.append(command);
            sb.append(System.lineSeparator());
        }
        getSharkMessengerUI().getOutStream().println(sb);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prints a log messages with all executed commands in the right order.");
        return sb.toString();
    }

    /**
     * Arguments needed in this order: 
     * <p>
     * none
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }

}
