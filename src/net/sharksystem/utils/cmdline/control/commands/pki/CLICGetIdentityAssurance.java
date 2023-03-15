package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICSharkPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

public class CLICGetIdentityAssurance extends CLICommand {

    private final CLICSharkPeerArgument owner;

    private final CLICSharkPeerArgument subject;

    public CLICGetIdentityAssurance(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.owner = new CLICSharkPeerArgument();
        this.subject = new CLICSharkPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Owner peer name: ", this.owner).
                addQuestion("Subject peer name: ", this.subject).
                build();
    }

    @Override
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIFromPeer(this.owner.getValue());
        int iA = pki.getIdentityAssurance(this.subject.getValue().getPeerID());

        StringBuilder sb = new StringBuilder();
        sb.append("Identity Assurance of ");
        sb.append(this.subject.getValue().getPeerID());
        sb.append("is: ");
        sb.append(iA);

        ui.printInfo(sb.toString());
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
