package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICSharkPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

public class CLICGetSigningFailureRate extends CLICommand {

    private final CLICSharkPeerArgument owner;

    private final CLICSharkPeerArgument subject;

    public CLICGetSigningFailureRate(String identifier, boolean rememberCommand) {
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
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        SharkPKIComponent pki = model.getPKIFromPeer(this.owner.getValue());
        int failureRate = pki.getSigningFailureRate(this.subject.getValue().getPeerID());

        StringBuilder sb = new StringBuilder();
        sb.append("Failure Rate of ");
        sb.append(this.subject.getValue().getPeerID());
        sb.append("is: ");
        sb.append(failureRate);

        ui.printInfo(sb.toString());
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns the signing failure rate of a specific peer.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
