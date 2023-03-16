package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

public class CLICGetSigningFailureRate extends CLICommand {

    private final CLICKnownPeerArgument subject;

    public CLICGetSigningFailureRate(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.subject = new CLICKnownPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Subject peer name: ", this.subject)
                .build();
    }

    @Override
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIComponent();
        int failureRate = pki.getSigningFailureRate(this.subject.getValue().getUserID());

        StringBuilder sb = new StringBuilder();
        sb.append("Failure Rate of ");
        sb.append(this.subject.getValue().getUserID());
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

}
