package net.sharksystem.utils.cmdline.control;

public class CLICQuestionnaireBuilder {

    private final CLICQuestionnaire questionnaire;

    public CLICQuestionnaireBuilder() {
        this.questionnaire = new CLICQuestionnaire();
    }

    public CLICQuestionnaireBuilder addQuestion(String question, CLICArgument<?> argument) {
        this.questionnaire.addQuestion(new CLICQuestion(question, argument));
        return this;
    }

    public CLICQuestionnaire build() {
        return this.questionnaire;
    }
}
