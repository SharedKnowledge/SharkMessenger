package net.sharksystem.utils.cmdline.control;

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

    public boolean submitAnswer(String input) throws Exception {
        return argument.tryParse(input);
    }

    public String getQuestionText() {
        return this.question;
    }
}
