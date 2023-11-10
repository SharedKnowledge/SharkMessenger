package net.sharksystem.cmdline.sharkmessengerUI;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.*;
import net.sharksystem.asap.apps.TCPServerSocketAcceptor;
import net.sharksystem.hub.HubConnectionManager;
import net.sharksystem.hub.HubConnectionManagerImpl;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerComponentFactory;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.Log;
import net.sharksystem.utils.streams.StreamPairImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
    private final ASAPEncounterManager encounterManager;
    private Map<Integer, TCPServerSocketAcceptor> openSockets = new HashMap<>();

    SharkMessengerApp(String peerName) throws SharkException, IOException {
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
            this.encounterManager = new ASAPEncounterManagerImpl((ASAPConnectionHandler) asapPeer, asapPeer.getPeerID());
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

    public SharkPKIComponent getSharkPKIComponent() {
        return this.pkiComponent;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                               TCP connection for batch tests                            //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public void openTCPConnection(int portNumber) throws IOException {
        TCPServerSocketAcceptor tcpServerSocketAcceptor =
                new TCPServerSocketAcceptor(portNumber, this.encounterManager);
        this.openSockets.put(portNumber, tcpServerSocketAcceptor);
        Log.writeLog(this, "Socket open to connect on port: " + portNumber);
    }

    public void connectOverTCP(String host, int portNumber) throws IOException {
        Socket socket = new Socket(host, portNumber);
        this.encounterManager.handleEncounter(StreamPairImpl.getStreamPair(
                socket.getInputStream(), socket.getOutputStream()), ASAPEncounterConnectionType.INTERNET);
        Log.writeLog(this, "connected to: " + host + " on port: " + portNumber);
    }

    public void closeTCPConnection(int portNumber) throws IOException {
        this.openSockets.remove(portNumber).close();
        Log.writeLog(this, "Socket closed on port: " + portNumber);
    }

}
