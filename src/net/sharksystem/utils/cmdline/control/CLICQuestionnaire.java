package net.sharksystem.utils.cmdline.control;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLICQuestionnaire {

    private final List<CLICQuestion> questionnaire;

    public CLICQuestionnaire() {
        this.questionnaire = new ArrayList<>();
    }

    public void addQuestion(CLICQuestion question) {
        this.questionnaire.add(question);
    }

    public void start(PrintStream ps, Scanner sc) {
        for(CLICQuestion question : this.questionnaire) {
            String userInput;
            do {
                question.ask(ps);
                userInput = sc.nextLine();
            } while (!question.submitAnswer(userInput));
        }
    }
}
