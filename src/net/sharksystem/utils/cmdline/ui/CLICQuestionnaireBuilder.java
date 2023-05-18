package net.sharksystem.utils.cmdline.ui;

/**
 * This builder makes it a bit easier to create a questionnaire by saving some lines of code for creating needed objects.
 */
public class CLICQuestionnaireBuilder {

    private final CLICQuestionnaire questionnaire;

    public CLICQuestionnaireBuilder() {
        this.questionnaire = new CLICQuestionnaire();
    }

    /**
     * Adds a question to the questionnaire and returns itself, so new questions can be added directly
     * @param question the question as string
     * @param argument the argument which parse logic is used, and in which the parsed value is stored
     * @return itself
     */
    public CLICQuestionnaireBuilder addQuestion(String question, CLICArgument<?> argument) {
        this.questionnaire.addQuestion(new CLICQuestion(question, argument));
        return this;
    }

    /**
     * Returns the questionnaire which was build
     */
    public CLICQuestionnaire build() {
        return this.questionnaire;
    }
}
