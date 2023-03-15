package net.sharksystem.utils.cmdline.control;

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

    public List<CLICQuestion> getQuestions() {
        return this.questionnaire;
    }
}
