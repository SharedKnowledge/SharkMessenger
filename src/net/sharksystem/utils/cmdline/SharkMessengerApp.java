package net.sharksystem.utils.cmdline;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.ASAPConnectionHandler;
import net.sharksystem.asap.ASAPEncounterManager;
import net.sharksystem.asap.ASAPEncounterManagerImpl;
import net.sharksystem.asap.ASAPPeer;
import net.sharksystem.hub.HubConnectionManager;
import net.sharksystem.hub.HubConnectionManagerImpl;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerComponentFactory;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.Log;

/**
 * Proposed and suggested pattern for Shark app. Implement a central entity (could even be a singleton)
 * that provides access to any component that is part of this application
 */
public class SharkMessengerApp {
    private final SharkPeerFS sharkPeerFS;

    private static final CharSequence ROOTFOLDER = "sharkMessengerDataStorage";
    private final SharkMessengerComponent messengerComponent;
    private final SharkPKIComponent pkiComponent;
    private final HubConnectionManager hubConnectionManager;

    SharkMessengerApp(String peerName) throws SharkException {
        this.sharkPeerFS = new SharkPeerFS(peerName, ROOTFOLDER + "/" + peerName);

        // set up shark components

        // get PKI factory
        SharkPKIComponentFactory pkiComponentFactory = new SharkPKIComponentFactory();

        // tell peer
        this.sharkPeerFS.addComponent(pkiComponentFactory, SharkPKIComponent.class);

        // get messenger factory with pki component as parameter.
        SharkMessengerComponentFactory messengerComponentFactory = new SharkMessengerComponentFactory(
                (SharkPKIComponent) sharkPeerFS.getComponent(SharkPKIComponent.class));

        // tell peer
        this.sharkPeerFS.addComponent(messengerComponentFactory, SharkMessengerComponent.class);

        // all component in place - start peer
        this.sharkPeerFS.start();

        // get component to add listener
        this.messengerComponent = (SharkMessengerComponent) this.sharkPeerFS.
                getComponent(SharkMessengerComponent.class);
        this.messengerComponent.addSharkMessagesReceivedListener(new MessageReceivedListener(this));

        // get component to add listener
        this.pkiComponent = (SharkPKIComponent) this.sharkPeerFS.getComponent(SharkPKIComponent.class);
        this.pkiComponent.setSharkCredentialReceivedListener(new CredentialReceivedListener(this));

        //////////////////////// setup hub connection management
        // TODO: that's still a design flaw. We need something that extracts a connection handler from a peer. Do we?
        ASAPPeer asapPeer = this.sharkPeerFS.getASAPPeer();
        // this code runs on service side - this peer should be a connection handler
        if (asapPeer instanceof ASAPConnectionHandler) { // TODO: aaaaargs
            // yes it is
            ASAPEncounterManager encounterManager = new ASAPEncounterManagerImpl((ASAPConnectionHandler) asapPeer);
            this.hubConnectionManager = new HubConnectionManagerImpl(encounterManager, asapPeer);
        } else {
            Log.writeLogErr(this,
                    "ASAP peer set but is not a connection handler - cannot set up connection management");
            throw new SharkException("Cannot set up connection management, see error logs.");
        }
    }

    public SharkPeer getSharkPeer() {
        return this.sharkPeerFS;
    }

    public SharkMessengerComponent getMessengerComponent() {
        return this.messengerComponent;
    }

    public HubConnectionManager getHubConnectionManager() throws SharkException {
        return this.hubConnectionManager;
    }
}
