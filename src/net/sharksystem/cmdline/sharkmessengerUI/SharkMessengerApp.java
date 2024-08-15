package net.sharksystem.cmdline.sharkmessengerUI;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.*;
import net.sharksystem.asap.apps.TCPServerSocketAcceptor;
import net.sharksystem.fs.ExtraData;
import net.sharksystem.fs.FSUtils;
import net.sharksystem.hub.HubConnectionManager;
import net.sharksystem.hub.HubConnectionManagerImpl;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerComponentFactory;
import net.sharksystem.pki.*;
import net.sharksystem.utils.Log;
import net.sharksystem.utils.streams.StreamPairImpl;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

/**
 * Proposed and suggested pattern for Shark app. Implement a central entity (could even be a singleton)
 * that provides access to any component that is part of this application
 */
public class SharkMessengerApp {
    private final SharkPeerFS sharkPeerFS;

    //private static final CharSequence ROOTFOLDER = "sharkMessengerDataStorage";
    private final SharkMessengerComponent messengerComponent;
    private final SharkPKIComponent pkiComponent;
    private final HubConnectionManager hubConnectionManager;
    private final ASAPEncounterManager encounterManager;
    private final ASAPEncounterManagerAdmin encounterManagerAdmin;
    private final String peerDataFolderName;
    private final ExtraData settings;
    private final String peerName;
    private Map<Integer, TCPServerSocketAcceptor> openSockets = new HashMap<>();
    private PrintStream outStream;
    private PrintStream errStream;

    public SharkMessengerApp(String peerName, ExtraData settings) throws SharkException, IOException {
        this.peerDataFolderName = "./" + peerName;
        this.settings = settings;
        this.sharkPeerFS = new SharkPeerFS(peerName, this.peerDataFolderName);
        this.peerName = peerName;

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

        //System.out.println("Fill PKI with example data - for testing purposes");
        //HelperPKITests.fillWithExampleData(this.pkiComponent);

        //////////////////////// setup hub connection management
        // TODO: that's still a design flaw. We need something that extracts a connection handler from a peer. Do we?
        ASAPPeer asapPeer = this.sharkPeerFS.getASAPPeer();
        // this code runs on service side - this peer should be a connection handler
        if (asapPeer instanceof ASAPConnectionHandler) { // TODO: aaaaargs
            // yes it is
            ASAPEncounterManagerImpl asapEncounterManager =
                    new ASAPEncounterManagerImpl((ASAPConnectionHandler) asapPeer, asapPeer.getPeerID());
            // same object - different roles
            this.encounterManager = asapEncounterManager;
            this.encounterManagerAdmin = asapEncounterManager;

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

    public SharkMessengerComponent getSharkMessengerComponent() {
        return this.messengerComponent;
    }

    public HubConnectionManager getHubConnectionManager() throws SharkException {
        return this.hubConnectionManager;
    }

    public SharkPKIComponent getSharkPKIComponent() {
        return this.pkiComponent;
    }

    public void destroyAllData() throws SharkException, IOException {
        FSUtils.removeFolder(this.peerDataFolderName);
        this.settings.removeAll();
    }

    public String getPeerName() {
        return this.peerName;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                    direct TCP connections                               //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public void openTCPConnection(int portNumber) throws IOException {
        TCPServerSocketAcceptor tcpServerSocketAcceptor =
                new TCPServerSocketAcceptor(portNumber, this.encounterManager, true);
        this.openSockets.put(portNumber, tcpServerSocketAcceptor);
        Log.writeLog(this, "Socket open to connect on port: " + portNumber);
    }

    public void connectTCP(String host, int portNumber) throws IOException {
        if(host.equalsIgnoreCase("127.0.0.1") || host.equalsIgnoreCase("localhost")) {
            if(this.openSockets.keySet().contains(portNumber)) {
                System.err.println("attempt to establish a connection to same process/peer refused");
                return;
            }
        }
        Socket socket = new Socket(host, portNumber);
        this.encounterManager.handleEncounter(StreamPairImpl.getStreamPair(
                socket.getInputStream(), socket.getOutputStream()), ASAPEncounterConnectionType.INTERNET);
        Log.writeLog(this, "connected to: " + host + " on port: " + portNumber);
    }

    public void closeTCPConnection(int portNumber) throws IOException {
        this.openSockets.remove(portNumber).close();
        Log.writeLog(this, "Socket closed on port: " + portNumber);
    }

    public Iterator<Integer> getOpenSockets() {
        return this.openSockets.keySet().iterator();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                    encounter management                                 //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public ASAPEncounterManagerAdmin getEncounterManagerAdmin() {
        return this.encounterManagerAdmin;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                    credential management                                //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private List<CredentialMessage> pendingCredentialMessages = new ArrayList<>();
    public void addPendingCredentialMessage(CredentialMessage credentialMessage) {
        this.pendingCredentialMessages.add(credentialMessage);
        Log.writeLog(this, "credentialMessage received");
        this.tellUI("credential message received .. please handle pending messages asap");
    }

    public List<CredentialMessage> getPendingCredentialMessages() {
        return this.pendingCredentialMessages;
    }


    public void actionOnPendingCredentialMessageOnIndex(int index, boolean accept)
            throws ASAPSecurityException, IOException {
        if (index < 1) {
            this.tellUI("\nminimal index is 1");
            return;
        }
        if(this.pendingCredentialMessages.size() < index) {
            this.tellUI("\nindex " + index + " exceeds maximum of " + this.pendingCredentialMessages.size());
            return;
        }
        // we are in the range
        index--; // adjust to internal counting .. we start with 0 as any normal person ;)
        CredentialMessage actionedCredential = this.pendingCredentialMessages.remove(index);
        if(accept) {
            this.getSharkPKIComponent().acceptAndSignCredential(actionedCredential);
            this.tellUI("\ncredential message accepted: \n" + PKIHelper.credentialMessage2String(actionedCredential));
        } else {
            this.tellUI("\ncredential message refused: \n" + PKIHelper.credentialMessage2String(actionedCredential));
        }

        this.tellUI("\nnote - indices of pending credential message has changed. Produce a new list before further actions.");
    }

    public void refusePendingCredentialMessageOnIndex(int index) throws ASAPSecurityException, IOException {
        this.actionOnPendingCredentialMessageOnIndex(index, false);
    }

    public void acceptPendingCredentialMessageOnIndex(int index) throws ASAPSecurityException, IOException {
        this.actionOnPendingCredentialMessageOnIndex(index, true);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                    communicate with UI                                  //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public void setUIStreams(PrintStream outStream, PrintStream errStream) {
        this.outStream = outStream;
        this.errStream = errStream;
    }

    public void tellUI(String message) {
        this.outStream.println(message);
    }

    public void tellUIError(String message) {
        this.errStream.println(message);
    }
}
