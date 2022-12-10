package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.util.List;

public abstract class CLICommand {

    private final boolean rememberCommand;
    private final String identifier;

    public CLICommand(String identifier, boolean rememberCommand) {
        this.identifier = identifier;
        this.rememberCommand = rememberCommand;
    }

    public abstract void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception;

    public String getIdentifier() { return this.identifier; };

    public boolean rememberCommand() { return this.rememberCommand; }

    public abstract String getDescription();


    public abstract String getDetailedDescription();
}
