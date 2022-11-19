package net.sharksystem.utils.cmdline.control;

import net.sharksystem.utils.cmdline.control.commands.CLICommand;

import java.util.List;

public interface CLIControllerStrategyInterface {

    void handleUserInput(String input);

    List<CLICommand> getCommands();
}
