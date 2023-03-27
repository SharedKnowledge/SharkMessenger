package net.sharksystem.utils.cmdline.model;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.ASAPConnectionHandler;
import net.sharksystem.asap.ASAPEncounterManager;
import net.sharksystem.asap.ASAPEncounterManagerImpl;
import net.sharksystem.hub.peerside.ASAPHubManager;
import net.sharksystem.hub.peerside.ASAPHubManagerImpl;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerComponentFactory;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;

/**
 * Standalone message app based on a SharkPeerFS.
 */
public class SharkMessengerApp {
    private static SharkMessengerApp app;
    private SharkPeer sharkPeer;
    private final ASAPEncounterManager encounterManager;
    private final ASAPHubManager asapHubManager;

    private SharkMessengerApp(CharSequence owner, CharSequence rootFolder) throws SharkException {
        // first - create a peer
        this.sharkPeer = new SharkPeerFS(owner, rootFolder);

        //////////////////////// setup components
        // PKI
        SharkPKIComponentFactory pkiFactory = new SharkPKIComponentFactory();
        this.sharkPeer.addComponent(pkiFactory, SharkPKIComponent.class);

        // Messenger
        SharkMessengerComponentFactory messengerFactory =
                new SharkMessengerComponentFactory(
                        (SharkPKIComponent) this.sharkPeer.getComponent(SharkPKIComponent.class));
        this.sharkPeer.addComponent(messengerFactory, SharkMessengerComponent.class);

        //////////////////////// setup connection management
        // TODO provide method on SharkPeer that produces a connection handler object
        ASAPConnectionHandler asapConnectionHandler =
                (ASAPConnectionHandler) this.sharkPeer.getASAPPeer();

        // create encounter manager
        this.encounterManager = new ASAPEncounterManagerImpl(asapConnectionHandler);

        // create hub manager
        this.asapHubManager = new ASAPHubManagerImpl(this.encounterManager);

        ////////////////////////// launch peer and system
        this.sharkPeer.start();
    }

    public static SharkMessengerApp getSharkMessengerApp(CharSequence owner, CharSequence rootFolder) throws SharkException {
        if(SharkMessengerApp.app == null) {
            SharkMessengerApp.app = new SharkMessengerApp(owner, rootFolder);
        }

        return SharkMessengerApp.app;
    }

    public SharkPeer getSharkPeer() { return this.sharkPeer;}
    public ASAPEncounterManager getASAPEncounterManager() { return this.encounterManager;}
    public ASAPHubManager getASAPHubManager() { return this.asapHubManager;}
}
