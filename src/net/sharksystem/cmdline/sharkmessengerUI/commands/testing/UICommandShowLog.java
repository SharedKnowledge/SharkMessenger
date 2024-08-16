package net.sharksystem.cmdline.sharkmessengerUI.commands.testing;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;

/**
 * Command for displaying the log history to the user.
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
        sb.append("Prints a log message with all executed commands in the right order.");
        return sb.toString();
    }

    /**
     * This command requires no arguments.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }

}
