package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

public class CLICGetKeysCreationTime extends CLICommand {

    private final CLICKnownPeerArgument peer;

    public CLICGetKeysCreationTime(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer = new CLICKnownPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Peer name: ", this.peer).
                build();
    }

    @Override
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIComponent();
        String creationTime = DateTimeHelper.long2DateString(pki.getKeysCreationTime());

        ui.printInfo("RSA key pairs were created at: " + creationTime);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns the time at which the RSA keys were created.");
        return sb.toString();
    }

}
