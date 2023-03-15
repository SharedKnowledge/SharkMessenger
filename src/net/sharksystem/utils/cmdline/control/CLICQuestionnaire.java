package net.sharksystem.utils.cmdline.control;

import java.util.ArrayList;
import java.util.List;

/**
 * A questionnaire storing questions in the order in which the questions were added.
 */
public class CLICQuestionnaire {

    /**
     * What the user needs to input in order to end the questionnaire
     */
    public static final String EXIT_SEQUENCE = "exit";

    private final List<CLICQuestion> questionnaire;

    public CLICQuestionnaire() {
        this.questionnaire = new ArrayList<>();
    }

    public void addQuestion(CLICQuestion question) {
        this.questionnaire.add(question);
    }

    public List<CLICQuestion> getQuestions() {
        return this.questionnaire;
    }
}
