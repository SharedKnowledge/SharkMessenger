package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.*;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

public class CLICSetSigningFailureRate extends CLICommand {

    private final CLICSharkPeerArgument owner;
    private final CLICSharkPeerArgument subject;

    private final CLICIntegerArgument failureRate;

    public CLICSetSigningFailureRate(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.owner = new CLICSharkPeerArgument();
        this.subject = new CLICSharkPeerArgument();
        this.failureRate = new CLICIntegerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Owner peer name: ", this.owner).
                addQuestion("Subject peer name: ", this.subject).
                addQuestion("Failure rate: ", this.failureRate).
                build();
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        SharkPKIComponent pki = model.getPKIFromPeer(this.owner.getValue());
        if(this.failureRate.getValue() >= 1 && this.failureRate.getValue() <= 10) {
            pki.setSigningFailureRate(this.subject.getValue().getPeerID(), this.failureRate.getValue());
        } else {
            ui.printError("Failure rate must be between 1 and 10 (1 and 10 inclusive)!");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sets the signing failure rate for a specific peer.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
