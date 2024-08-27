package net.sharksystem.messenger.cli;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkPeerEncounterChangedListener;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.*;
import net.sharksystem.asap.apps.TCPServerSocketAcceptor;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.asap.utils.PeerIDHelper;
import net.sharksystem.fs.ExtraData;
import net.sharksystem.fs.ExtraDataFS;
import net.sharksystem.fs.FSUtils;
import net.sharksystem.hub.HubConnectionManager;
import net.sharksystem.hub.HubConnectionManagerImpl;
import net.sharksystem.hub.hubside.ASAPTCPHub;
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
public class SharkMessengerApp implements SharkPeerEncounterChangedListener {
    private static final CharSequence PEER_ID_KEY = "peerIDKey";
    private final SharkPeerFS sharkPeerFS;

    // keystore
    private static final CharSequence KEYSTORE_MEMENTO_KEY = "keyStoreMemento";

    //private static final CharSequence ROOTFOLDER = "sharkMessengerDataStorage";
    private final SharkMessengerComponent messengerComponent;
    private final SharkPKIComponent pkiComponent;
    private final HubConnectionManager hubConnectionManager;
    private final ASAPEncounterManager encounterManager;
    private final ASAPEncounterManagerAdmin encounterManagerAdmin;
    private final String peerDataFolderName;
    private final ExtraData appSettings;
    private final String peerName;
    private PrintStream outStream;
    private PrintStream errStream;
    private CharSequence peerID;

    public SharkMessengerApp(String peerName, PrintStream out, PrintStream err)
            throws SharkException, IOException {
        this(peerName, 60*10, out, err);
    }

    public SharkMessengerApp(String peerName, int syncWithOthersInSeconds, PrintStream out, PrintStream err)
            throws SharkException, IOException {

        this.peerDataFolderName = "./" + peerName;
        this.appSettings = new ExtraDataFS(".", "SharkMessengerSetting" + peerName);
        this.outStream = out;
        this.errStream = err;
        this.sharkPeerFS = new SharkPeerFS(peerName, this.peerDataFolderName);
        this.peerName = peerName;

        try {
            this.peerID = this.sharkPeerFS.getSharkPeerExtraData().getExtraString(PEER_ID_KEY);
            this.tellUI("asap peer id for " + peerName + " is " + this.peerID);
        }
        catch (SharkException se) {
            this.peerID = peerName + "_" + PeerIDHelper.createUniqueID();
            this.tellUI("created a new asap peer id for " + peerName + ": " + this.peerID);
            this.sharkPeerFS.getSharkPeerExtraData().putExtra(PEER_ID_KEY, this.peerID);
        }

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
        this.sharkPeerFS.start(this.peerID);

        // get component to add listener
        this.messengerComponent = (SharkMessengerComponent) this.sharkPeerFS.
                getComponent(SharkMessengerComponent.class);
        this.messengerComponent.addSharkMessagesReceivedListener(new MessageReceivedListener(this));

        // get component to add listener
        this.pkiComponent = (SharkPKIComponent) this.sharkPeerFS.getComponent(SharkPKIComponent.class);
        this.pkiComponent.setSharkCredentialReceivedListener(new CredentialReceivedListener(this));

        //System.out.println("Fill PKI with example data - for testing purposes");
        //HelperPKITests.fillWithExampleData(this.pkiComponent);

        // get informed about encounter changes
        this.sharkPeerFS.addSharkPeerEncounterChangedListener(this);

        //////////////////////// setup hub connection management
        // TODO: that's still a design flaw. We need something that extracts a connection handler from a peer. Do we?
        ASAPPeer asapPeer = this.sharkPeerFS.getASAPPeer();

        ASAPConnectionHandler asapHandler = (ASAPConnectionHandler) asapPeer;
        // this code runs on service side - this peer should be a connection handler
        if (asapPeer instanceof ASAPConnectionHandler) { // TODO: aaaaargs
            // yes it is
            ASAPEncounterManagerImpl asapEncounterManager =
                    new ASAPEncounterManagerImpl(asapHandler, asapPeer.getPeerID(),
                            syncWithOthersInSeconds*1000);
            // same object - different roles
            this.encounterManager = asapEncounterManager;
            this.encounterManagerAdmin = asapEncounterManager;

            this.hubConnectionManager =
//                    new HubConnectionManagerImpl(this.encounterManager, asapPeer, syncWithOthersInSeconds);
                  new HubConnectionManagerImpl(this.encounterManager, asapPeer); // default time is okay
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
        this.appSettings.removeAll();
    }

    public String getPeerName() {
        return this.peerName;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                    direct TCP connections                               //
    /////////////////////////////////////////////////////////////////////////////////////////////
    private Map<Integer, TCPServerSocketAcceptor> openSockets = new HashMap<>();

    private boolean portAlreadyInUse(int port) {
        // hub using that port?
        Object something = this.asapHubs.get(port);
        if(something == null) {
            // an open tcp port?
            something = this.openSockets.get(port);
            if (something == null) return false; // exit - port is available
        }

        tellUI("port already in use - choose another one");
        return true;
    }

    public void openTCPConnection(int portNumber) throws IOException {
        if(this.portAlreadyInUse(portNumber)) return;
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
    //                                         hub management                                  //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private Map<Integer, ASAPTCPHub> asapHubs = new HashMap<>();

    public Set<Integer> getOpenHubPorts() {
        return this.asapHubs.keySet();
    }

    public void startHub(int portNumber) throws IOException {
        if(this.portAlreadyInUse(portNumber)) return;

        // create a new hub that spawns new TCP connections.
//        ASAPTCPHub asapHub = new ASAPTCPHub(portNumber, true);

        ASAPTCPHub asapHub = new ASAPTCPHub(portNumber, false);
        this.asapHubs.put(portNumber, asapHub);
        new Thread(asapHub).start();
    }

    public void stopHub(int portNumber) throws IOException {
        ASAPTCPHub asapHub = this.asapHubs.remove(portNumber);
        if(asapHub == null) {
            this.tellUIError("there is no ASAP hub listening on port " + portNumber);
            return;
        }
        // stop it
        asapHub.kill();
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
            try {
                this.getSharkPKIComponent().getPersonValuesByName(actionedCredential.getSubjectName());
                this.tellUI("we already know a peer with that name - take id instead their name for your address book");
                actionedCredential.setSubjectName(actionedCredential.getSubjectID());
            }
            catch(ASAPException ae) {
                this.tellUI("You do not know a peer with that name yet.");
            }

            this.getSharkPKIComponent().acceptAndSignCredential(actionedCredential);
            this.tellUI("\nissued certificate based on:\n" + PKIHelper.credentialMessage2String(actionedCredential));
        } else {
            this.tellUI("\nrefused to issue a certificate:\n" + PKIHelper.credentialMessage2String(actionedCredential));
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
    //                                 environment changed handling                            //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void encounterStarted(CharSequence peerID) {
        this.tellUI("\nnew encounter: " + peerID);

        // check if better ask for a (fresh) certificate?
        try {
            try {
                ASAPCertificate certificateByIssuerAndSubject =
                        this.getSharkPKIComponent().
                                getCertificateByIssuerAndSubject(peerID, this.getSharkPeer().getPeerID());

                ;
                StringBuilder sb = new StringBuilder();
                sb.append("\nYou have an encounter with peer ");
                sb.append(peerID);
                sb.append(". It issued a certificate for you that runs out ");
                sb.append(DateTimeHelper.
                        long2DateString(certificateByIssuerAndSubject.getValidUntil().getTimeInMillis()));
                this.tellUI(sb.toString());
            }
            catch(ASAPSecurityException ase) {
                StringBuilder sb = new StringBuilder();
                sb.append("\nPeer ");
                sb.append(peerID);
                sb.append(" has not yet issued a certificate for you. You are connected now. A good time to ask for one?");
                this.tellUI(sb.toString());
            }
        } catch (SharkException e) {
            this.tellUIError("unexpected problems when dealing with present certificates: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void encounterTerminated(CharSequence peerID) {
        this.tellUI("\nterminated encounter: " + peerID);
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
