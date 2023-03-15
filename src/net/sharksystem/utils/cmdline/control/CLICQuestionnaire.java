package net.sharksystem.utils.cmdline.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A questionnaire storing questions in the order in which the questions were added.
 */
public class CLICQuestionnaire {

    private final List<CLICQuestion> questionnaire;

    public CLICQuestionnaire() {
        this.questionnaire = new ArrayList<>();
    }

    public void addQuestion(CLICQuestion question) {
        this.questionnaire.add(question);
    }

    /**
     * Starts the questionnaire. The user gets prompted with every question and his answer will be stored if it was
     * successfully parsed. If the argument logic couldn't parse the input, the user is asked again.
     * @param ps The PrintStream to which the question is written
     */
    public void start(PrintStream ps, BufferedReader bufferedReader) {
        for(CLICQuestion question : this.questionnaire) {
            String userInput = "";
            do {
                question.ask(ps);
                try {
                    userInput = bufferedReader.readLine();
                } catch (IOException e) {
                    ps.println(e.getLocalizedMessage());
                }
            } while (!question.submitAnswer(userInput));
            CLIController.getModel().addCommandToHistory(userInput);
        }
    }
}
