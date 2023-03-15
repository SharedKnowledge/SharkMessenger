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
     * @param ui The user interface
     * @param model The model
     * @throws Exception which might occur when executing the command.
     */
    public void startCommandExecution(CLIInterface ui, CLIModelInterface model) throws Exception {
        CLICQuestionnaire questionnaire = this.specifyCommandStructure();
        if(questionnaire != null) {
            ui.letUserFillOutQuestionnaire(questionnaire);
        }
        this.execute(ui, model);
    }

    /**
     * This function returns a questionnaire wich the user needs to answer.
     * The questionnaire is defined in the class of the command. It is recommended to use a CLICQuestionnaireBuilder
     * for setting up the questions.
     * @return A questionnaire wich is just an ordered list of questions
     */
    public abstract CLICQuestionnaire specifyCommandStructure();

    /**
     * This method includes the logic of the command
     * @param ui the user interface
     * @param model the model
     * @throws Exception any exception that might be thrown
     */
    public abstract void execute(CLIInterface ui, CLIModelInterface model) throws Exception;

    public String getIdentifier() { return this.identifier; }

    public boolean rememberCommand() { return this.rememberCommand; }

    public abstract String getDescription();


    public abstract String getDetailedDescription();
}
