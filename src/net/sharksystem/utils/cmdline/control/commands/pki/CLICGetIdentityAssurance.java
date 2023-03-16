package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

public class CLICGetIdentityAssurance extends CLICommand {

    private final CLICKnownPeerArgument subject;

    public CLICGetIdentityAssurance(String identifier, boolean rememberCommand) {
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
        int iA = pki.getIdentityAssurance(this.subject.getValue().getUserID());

        StringBuilder sb = new StringBuilder();
        sb.append("Identity Assurance of ");
        sb.append(this.subject.getValue().getUserID());
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

}
