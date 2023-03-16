package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

public class CLICCreateCredentialMessage extends CLICommand {
    public CLICCreateCredentialMessage(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a credential message which can be send to another peer.");
        return sb.toString();
    }

}
