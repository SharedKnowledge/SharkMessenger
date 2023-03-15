package net.sharksystem.utils.cmdline.control.commands.general;

import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

/**
 * Command for terminating the messanger
 */
public class CLICExit extends CLICommand {


    public CLICExit(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        model.terminate();
    }


    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Terminates the messanger.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
