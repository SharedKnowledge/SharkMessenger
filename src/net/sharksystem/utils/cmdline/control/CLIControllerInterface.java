package net.sharksystem.utils.cmdline.control;

/**
 * Interface to extend the functionalities of the CLIController
 */
public interface CLIControllerInterface {

    /**
     * Adds a new command to the controller which will be executed if the user inputs its identifier
     * @param command The command that should be added.
     */
    void addCommand(CLICommand command);

    /**
     * Starts the CLI
     */
    void startCLI();
}
