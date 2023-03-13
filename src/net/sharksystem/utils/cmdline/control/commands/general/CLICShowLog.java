package net.sharksystem.utils.cmdline.control.commands.general;

import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

/**
 * Command for displaying the log history to the user
 */
public class CLICShowLog extends CLICommand {

    public CLICShowLog(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        ui.printInfo(model.getCommandHistory());
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prints a log messages with all executed commands in the right order.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}
