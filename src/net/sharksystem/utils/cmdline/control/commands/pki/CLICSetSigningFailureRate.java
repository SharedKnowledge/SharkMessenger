package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.*;

public class CLICSetSigningFailureRate extends CLICommand {
    private final CLICKnownPeerArgument subject;

    private final CLICIntegerArgument failureRate;

    public CLICSetSigningFailureRate(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.subject = new CLICKnownPeerArgument();
        this.failureRate = new CLICIntegerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Subject peer name: ", this.subject)
                .addQuestion("Failure rate: ", this.failureRate)
                .build();
    }

    @Override
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIComponent();
        if(this.failureRate.getValue() >= 1 && this.failureRate.getValue() <= 10) {
            pki.setSigningFailureRate(this.subject.getValue().getUserID(), this.failureRate.getValue());

            StringBuilder sb = new StringBuilder();
            sb.append("Failure rate was set to ");
            sb.append(this.failureRate.getValue());
            sb.append(" for peer ");
            sb.append(this.subject.getValue().getUserID());

            ui.printInfo(sb.toString());

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

}
