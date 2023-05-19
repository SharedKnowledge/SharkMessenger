package net.sharksystem.utils.cmdline.ui.commands.general;

import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICommand;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;

/**
 * Command for displaying the log history to the user.
 */
public class CLICShowLog extends CLICommand {

    public CLICShowLog(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                       String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute() throws Exception {
        ui.printInfo(model.getCommandHistory());
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prints a log messages with all executed commands in the right order.");
        return sb.toString();
    }

}
