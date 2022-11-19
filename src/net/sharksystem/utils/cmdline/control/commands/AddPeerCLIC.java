package net.sharksystem.utils.cmdline.control.commands;


import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerComponentFactory;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.util.List;

public class AddPeerCLIC extends CLICommand {

    public AddPeerCLIC(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) {
        if (args.size() == 1) {
            String peerName = args.get(0);
            if (model.hasPeer(peerName)) {
                ui.printError("Peer " + peerName + " already exists!");
            } else {
                String peerFolderName = peerName + "_PEER_FOLDER";
                SharkTestPeerFS.removeFolder(peerFolderName);
                SharkTestPeerFS peer = new SharkTestPeerFS(peerName, peerFolderName);

                try {
                    SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
                    // register this component with shark peer - note: we use interface SharkPeer
                    peer.addComponent(certificateComponentFactory, SharkPKIComponent.class);

                    ui.printInfo("Successfully added PKIComponent to peer " + peerName);

                    SharkMessengerComponentFactory messengerFactory =
                            new SharkMessengerComponentFactory(
                                    (SharkPKIComponent) peer.getComponent(SharkPKIComponent.class)
                            );
                    peer.addComponent(messengerFactory, SharkMessengerComponent.class);

                    ui.printInfo("Successfully added MessengerComponent to peer " + peerName);

                    model.addPeer(peerName, peer);
                    peer.start();

                    ui.printInfo(peerName + " was started!");

                } catch (SharkException e) {
                    ui.printError(e.getLocalizedMessage());
                }

            }

        } else {
            ui.printError("Not enough arguments where specified calling " + this.getIdentifier() + " command!");
            ui.printError("See following usage:");
            ui.printError(this.getDetailedDescription());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("create a new peer");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("create a new peer");
        //TODO: detailed description
        return sb.toString();
    }
}
