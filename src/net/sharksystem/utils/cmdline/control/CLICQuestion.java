package net.sharksystem.utils.cmdline.control;

import java.io.PrintStream;

/**
 * A question which can be asked and answered.
 */
public class CLICQuestion {

    /**
     * The question
     */
    private final String question;

    /**
     * The argument in which the user input is stored
     */
    private final CLICArgument<?> argument;

    public CLICQuestion(String question, CLICArgument<?> argument) {
        this.question = question;
        this.argument = argument;
    }

    boolean submitAnswer(String input) {
        return argument.tryParse(input);
    }

    void ask(PrintStream ps){
        ps.print(question);
    }
}
