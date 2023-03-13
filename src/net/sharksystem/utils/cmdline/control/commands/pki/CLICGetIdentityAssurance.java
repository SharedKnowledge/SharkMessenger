package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

public class CLICGetIdentityAssurance extends CLICommand {
    public CLICGetIdentityAssurance(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {

    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns the identity assurance of a specific peer.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
