package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICSharkPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

import java.util.List;

public class CLICGetCertificationPath extends CLICommand {

    private final CLICSharkPeerArgument subject;

    public CLICGetCertificationPath(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.subject = new CLICSharkPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Subject name: ", this.subject).
                build();
    }

    @Override
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIFromPeer(this.subject.getValue());
        List<CharSequence> path = pki.getIdentityAssurancesCertificationPath(this.subject.getValue().getPeerID());

        StringBuilder sb = new StringBuilder();
        for(CharSequence userID : path) {
            sb.append(" -> ");
            sb.append(userID);
        }
        ui.printInfo(sb.toString());
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns the path which a certificate took.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
