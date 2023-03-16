package net.sharksystem.utils.cmdline.control.commands.messenger;


import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerComponentFactory;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.control.CLICStringArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;

public class CLICAddPeer extends CLICommand {

    private final CLICStringArgument name;

    public CLICAddPeer(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.name = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Please insert the peer name: ", this.name).
                build();
    }


    @Override
    public void execute() throws Exception {
        String peerName = this.name.getValue();
        if (model.hasPeer(peerName)) {

            ui.printError("Peer " + peerName + " already exists!");
        } else {
            String peerFolderName = "CLI/PEERS/" + peerName + "_PEER_FOLDER";
            SharkTestPeerFS.removeFolder(peerFolderName);
            SharkTestPeerFS peer = new SharkTestPeerFS(peerName, peerFolderName);

            try {
                SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
                // register this component with shark peer - note: we use interface SharkPeer
                peer.addComponent(certificateComponentFactory, SharkPKIComponent.class);

                ui.printInfo("Successfully added PKIComponent to peer " + peerName);

                SharkMessengerComponentFactory messengerFactory = new SharkMessengerComponentFactory(
                        (SharkPKIComponent) peer.getComponent(SharkPKIComponent.class));
                peer.addComponent(messengerFactory, SharkMessengerComponent.class);

                ui.printInfo("Successfully added MessengerComponent to peer " + peerName);

                model.addPeer(peerName, peer);
                peer.start();

                model.getPKIFromPeer(peer).setSharkCredentialReceivedListener(credentialMessage ->
                        ui.displayCredentialMessage(credentialMessage));

                ui.printInfo(peerName + " was started!");

            } catch (SharkException e) {
                ui.printError(e.getLocalizedMessage());
            }

        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new peer.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }

}
