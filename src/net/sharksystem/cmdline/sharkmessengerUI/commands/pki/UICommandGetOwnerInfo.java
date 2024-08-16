package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandKnownPeerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;

public class UICommandGetOwnerInfo extends UICommand {

    private final UICommandKnownPeerArgument peer;

    public UICommandGetOwnerInfo(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                 String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.peer = new UICommandKnownPeerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder().
                addQuestion("Peer name: ", this.peer).
                build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        CharSequence ownerID = pki.getOwnerID();
        CharSequence ownerName = pki.getOwnerName();

        StringBuilder sb = new StringBuilder();
        sb.append("OwnerID: ");
        sb.append(ownerID);
        sb.append("\tOwnerName: ");
        sb.append(ownerName);
        ui.printInfo(sb.toString());

 */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns information about a specific peer.");
        return sb.toString();
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>peer - peerID</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            return false;
        }
        
        boolean isParsable = peer.tryParse(arguments.get(0));
        return isParsable;
    }
}