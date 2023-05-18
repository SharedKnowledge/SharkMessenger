package net.sharksystem.utils.cmdline.ui.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.ui.CLICommand;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;

public class CLICGetOwnerInfo extends CLICommand {

    private final CLICKnownPeerArgument peer;

    public CLICGetOwnerInfo(SharkMessengerApp sharkMessengerApp, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, identifier, rememberCommand);
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
        CharSequence ownerID = pki.getOwnerID();
        CharSequence ownerName = pki.getOwnerName();

        StringBuilder sb = new StringBuilder();
        sb.append("OwnerID: ");
        sb.append(ownerID);
        sb.append("\tOwnerName: ");
        sb.append(ownerName);
        ui.printInfo(sb.toString());
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns information about a specific peer.");
        return sb.toString();
    }

}
