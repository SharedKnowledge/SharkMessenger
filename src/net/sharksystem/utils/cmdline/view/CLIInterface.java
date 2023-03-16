package net.sharksystem.utils.cmdline.view;

import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

/**
 * Interface that allows the CLIController to inform the CLI over any changes
 */
public interface CLIInterface {

    /**
     * Prints an info for the user to the command line.
     * @param information The message.
     */
    void printInfo(String information);

    /**
     * Prints an error to the command line.
     * @param error The error message.
     */
    void printError(String error);

    /**
     * Displays each question from the given questionnaire to the user.
     * The answers from the user is fed back into the current question. If the answer is valid, the next question is
     * asked. If the answer isn't valid by the definition of the argument behind the question, an error message is shown
     * with the reason, why the input wasn't valid.
     * @param questionnaire The questionnaire which the user needs to fill out.
     * @return True, if this process wasn't terminated by the user; False otherwise.
     */
    boolean letUserFillOutQuestionnaire(CLICQuestionnaire questionnaire);

    /**
     * Displays a message that the command couldn't be found.
     * @param commandIdentifier The identifier the user though would start a valid command.
     */
    void commandNotFound(String commandIdentifier);

    /**
     * Displays a message that the specified command was terminated while execution.
     * More precisely: While the user entered the arguments needed.
     * @param identifier The identifier for the terminated command.
     */
    void commandWasTerminated(String identifier);
}
