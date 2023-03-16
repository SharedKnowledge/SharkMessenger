package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

public class CLICCreateNewKeyPair extends CLICommand {

    public CLICCreateNewKeyPair(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIComponent();
        pki.createNewKeyPair();
        String creationTime = DateTimeHelper.long2DateString(pki.getKeysCreationTime());

        ui.printInfo("New RSA key pair was created at: " + creationTime);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new pair of RSA keys.");
        return sb.toString();
    }

}
