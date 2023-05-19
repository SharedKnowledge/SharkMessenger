package net.sharksystem.utils.cmdline.ui.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.ui.CLICommand;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;

public class CLICGetIdentityAssurance extends CLICommand {

    private final CLICKnownPeerArgument subject;

    public CLICGetIdentityAssurance(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                    String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
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
