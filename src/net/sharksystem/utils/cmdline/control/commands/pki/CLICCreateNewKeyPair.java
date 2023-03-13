package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICSharkPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

public class CLICCreateNewKeyPair extends CLICommand {

    private final CLICSharkPeerArgument peer;

    public CLICCreateNewKeyPair(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer = new CLICSharkPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Peer name: ", this.peer).
                build();
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        SharkPKIComponent pki = (SharkPKIComponent) this.peer.getValue().getComponent(SharkPKIComponent.class);
        String creationTime = DateTimeHelper.long2DateString(pki.getKeysCreationTime());

        ui.printInfo("New RSA key pair was created at: " + creationTime);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new pair of RSA keys.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
