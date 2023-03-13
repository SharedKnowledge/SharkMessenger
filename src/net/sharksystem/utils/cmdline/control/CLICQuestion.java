package net.sharksystem.utils.cmdline.control;

import java.io.PrintStream;

public class CLICQuestion {
    private final String question;
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
