package net.sharksystem.utils.cmdline.control;

import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

public abstract class CLICommand {

    private final boolean rememberCommand;
    private final String identifier;

    public CLICommand(String identifier, boolean rememberCommand) {
        this.identifier = identifier;
        this.rememberCommand = rememberCommand;
    }

    public void startCommandExecution(CLIInterface ui, CLIModelInterface model) throws Exception {
        CLICQuestionnaire questionnaire = this.specifyCommandStructure();
        if(questionnaire != null) {
            ui.letUserFillOutQuestionnaire(questionnaire);
        }
        this.execute(ui, model);
    }

    public abstract CLICQuestionnaire specifyCommandStructure();

    public abstract void execute(CLIInterface ui, CLIModelInterface model) throws Exception;

    public String getIdentifier() { return this.identifier; }

    public boolean rememberCommand() { return this.rememberCommand; }

    public abstract String getDescription();


    public abstract String getDetailedDescription();
}
