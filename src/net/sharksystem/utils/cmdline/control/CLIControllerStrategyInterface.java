package net.sharksystem.utils.cmdline.control;

import java.util.List;

/**
 * Interface between CLIController and CLIView.
 */
public interface CLIControllerStrategyInterface {

    /**
     * Accepts a String as user input and performs it's meaning if it can be interpreted
     * @param input the user input
     */
    void handleUserInput(String input) throws Exception;

    /**
     * Returns a list of all valid commands
     * @return the list of valid commands
     */
    List<CLICommand> getCommands();

    void logQuestionAnswer(String userInput);
}
