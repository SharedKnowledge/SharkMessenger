package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICSharkPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

public class CLICGetOwnerInfo extends CLICommand {

    private final CLICSharkPeerArgument peer;

    public CLICGetOwnerInfo(String identifier, boolean rememberCommand) {
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
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIFromPeer(this.peer.getValue());
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

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
