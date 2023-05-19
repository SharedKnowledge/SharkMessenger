package net.sharksystem.utils.cmdline;

import net.sharksystem.SharkConnectionManager;
import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.ASAPEncounterManager;
import net.sharksystem.asap.ASAPEncounterManagerImpl;
import net.sharksystem.hub.peerside.ASAPHubManager;
import net.sharksystem.hub.peerside.ASAPHubManagerImpl;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerComponentFactory;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.serviceSide.SharkPeerFSServiceSide;

/**
 * Proposed and suggested pattern for Shark app. Implement a central entity (could even be a singleton)
 * that provides access to any component that is part of this application
 */
public class SharkMessengerApp {
    private final SharkPeerFS sharkPeerFS;

    private static final CharSequence ROOTFOLDER = "sharkMessenger";
    private final SharkMessengerComponent messengerComponent;
    private final SharkPKIComponent pkiComponent;

    SharkMessengerApp(String peerName) throws SharkException {
        this.sharkPeerFS = new SharkPeerFSServiceSide(peerName, ROOTFOLDER + "/" + peerName);

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
    }

    public SharkPeer getSharkPeer() {
        return this.sharkPeerFS;
    }

    public SharkMessengerComponent getMessengerComponent() {
        return this.messengerComponent;
    }

    public SharkConnectionManager getSharkConnectionManager() throws SharkException {
        return this.sharkPeerFS.getSharkConnectionManager();
    }
}
