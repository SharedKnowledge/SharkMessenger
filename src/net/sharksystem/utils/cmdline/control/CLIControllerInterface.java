package net.sharksystem.utils.cmdline.control;

import net.sharksystem.utils.cmdline.control.commands.CLICommand;

public interface CLIControllerInterface {

    void addCommand(CLICommand command);

    void startCLI();
}
