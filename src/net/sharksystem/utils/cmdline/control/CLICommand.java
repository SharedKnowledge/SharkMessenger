package net.sharksystem.utils.cmdline.control;

import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

/**
 * Command which can be executed from the command line.
 */
public abstract class CLICommand {
    /**
     * If this flag is true, the command is saved in a log which can be used for testing a specific scenario.
     */
    private final boolean rememberCommand;

    /**
     * The name of the command.
     */
    private final String identifier;

    /**
     * The model
     */
    protected static CLIModelInterface model = CLIController.getModel();

    /**
     * The ui
     */
    protected static CLIInterface ui = CLIController.getView();

    /**
     * The controller
     */
    protected static CLIControllerInterface controller = CLIController.getController();

    /**
     * Creates a command object.
     * @param identifier The identifier of the command.
     * @param rememberCommand If the command should be saved in the history log.
     */
    public CLICommand(String identifier, boolean rememberCommand) {
        this.identifier = identifier;
        this.rememberCommand = rememberCommand;
    }

    /**
     * This method runs the command. It asks the user for any input that is needed and was defined in the class of the
     * command which is run.
     * @throws Exception Which might occur when executing the command.
     */
    public void startCommandExecution() throws Exception {
        CLICQuestionnaire questionnaire = this.specifyCommandStructure();
        if(questionnaire != null && !CLIController.getView().letUserFillOutQuestionnaire(questionnaire)) {
            ui.commandWasTerminated(this.identifier);
        } else {
            this.execute();
        }
    }

    /**
     * This function returns a questionnaire wich the user needs to answer.
     * The questionnaire is defined in the class of the command. It is recommended to use a CLICQuestionnaireBuilder
     * for setting up the questions.
     * @return A questionnaire wich is just an ordered list of questions.
     */
    protected abstract CLICQuestionnaire specifyCommandStructure();

    /**
     * This method includes the logic of the command.
     * @throws Exception Any exception that might be thrown.
     */
    protected abstract void execute() throws Exception;

    /**
     * @return The string which identifies this command and can be entered by the user to execute it.
     */
    public String getIdentifier() { return this.identifier; }

    /**
     * @return Whether this command should be saved in a log file when the user wishes to do so or not.
     */
    public boolean rememberCommand() { return this.rememberCommand; }

    /**
     * @return A short description of what the command does.
     */
    public abstract String getDescription();


}
