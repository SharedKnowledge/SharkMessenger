package net.sharksystem.utils.cmdline.control;

import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

/**
 * Command which can be executed from the command line
 */
public abstract class CLICommand {
    /**
     * If this flag is true, the command is saved in a log which can be used for testing a specific scenario
     */
    private final boolean rememberCommand;

    /**
     * The name of the command
     */
    private final String identifier;

    protected static CLIModelInterface model = CLIController.getModel();

    protected static CLIInterface ui = CLIController.getView();
    protected static CLIControllerInterface controller = CLIController.getController();

    /**
     * Creates a command object
     * @param identifier the identifier of the command
     * @param rememberCommand if the command should be saved in the history log
     */
    public CLICommand(String identifier, boolean rememberCommand) {
        this.identifier = identifier;
        this.rememberCommand = rememberCommand;
    }

    /**
     * This method runs the command. It asks the user for any input that is needed and was defined in the class of the
     * command which is run.
     * @throws Exception which might occur when executing the command.
     */
    public void startCommandExecution() throws Exception {
        CLICQuestionnaire questionnaire = this.specifyCommandStructure();
        if(questionnaire != null && !CLIController.getView().letUserFillOutQuestionnaire(questionnaire)) {
            ui.printInfo("Stopped command execution " + this.identifier);
        } else {
            this.execute();
        }
    }

    /**
     * This function returns a questionnaire wich the user needs to answer.
     * The questionnaire is defined in the class of the command. It is recommended to use a CLICQuestionnaireBuilder
     * for setting up the questions.
     * @return A questionnaire wich is just an ordered list of questions
     */
    protected abstract CLICQuestionnaire specifyCommandStructure();

    /**
     * This method includes the logic of the command
     *
     * @throws Exception any exception that might be thrown
     */
    protected abstract void execute() throws Exception;

    public String getIdentifier() { return this.identifier; }

    public boolean rememberCommand() { return this.rememberCommand; }

    public abstract String getDescription();


    public abstract String getDetailedDescription();
}
