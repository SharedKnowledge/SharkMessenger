package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.asap.*;
import net.sharksystem.asap.apps.TCPServerSocketAcceptor;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.fs.FSUtils;
import net.sharksystem.utils.streams.StreamPairImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class ASAPHubConnectionTest {
    private String TEST_FOLDER;
    private CharSequence EXAMPLE_APP_FORMAT = "shark/x-connectPeersExample";
    private int portNumberAlice = 6000;
    private int portNumberBob = 6001;

    @BeforeEach
    public void init(){
        // get current user dir
        String currentDir = System.getProperty("user.dir");
        TEST_FOLDER = currentDir + "/ASAPHubConnectionTest";
        // delete test dir if already exists
        FSUtils.removeFolder(TEST_FOLDER);
    }


    @Test
    public void sendMessageUsingTCPSocket() throws IOException, SharkException, InterruptedException {
        Collection<CharSequence> formats = new ArrayList<>();
        formats.add(EXAMPLE_APP_FORMAT);
        String aliceFolder = TEST_FOLDER + "/" + TestConstants.ALICE_ID;
        String bobFolder = TEST_FOLDER + "/" + TestConstants.BOB_ID;

        ////////////////////////// set up peer alice
        SharkPeerFS alice = new SharkPeerFS(TestConstants.ALICE_ID, aliceFolder);

        SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
        // register this component with shark peer - note: we use interface SharkPeer
        alice.addComponent(certificateComponentFactory, SharkPKIComponent.class);
        SharkMessengerComponentFactory messengerFactory = new SharkMessengerComponentFactory(
                (SharkPKIComponent) alice.getComponent(SharkPKIComponent.class));
        alice.addComponent(messengerFactory, SharkMessengerComponent.class);
        alice.start();

        ////////////////////////// set up peer bob
        SharkPeerFS bob = new SharkPeerFS(TestConstants.BOB_ID, bobFolder);

        certificateComponentFactory = new SharkPKIComponentFactory();
        // register this component with shark peer - note: we use interface SharkPeer
        bob.addComponent(certificateComponentFactory, SharkPKIComponent.class);
        messengerFactory = new SharkMessengerComponentFactory(
                (SharkPKIComponent) bob.getComponent(SharkPKIComponent.class));
        bob.addComponent(messengerFactory, SharkMessengerComponent.class);
        bob.start();

        // alice creates new channel
        SharkMessengerComponent peerMessenger = (SharkMessengerComponent) alice.getComponent(SharkMessengerComponent.class);
        peerMessenger.createChannel("my_channel/test", "aliceChannel");

        // alice sends a message to bob
        peerMessenger.sendSharkMessage("Hi Bob".getBytes(), "my_channel/test", false, false);

        ASAPConnectionHandler aliceConnectionHandler = (ASAPConnectionHandler) alice.getASAPPeer();
        ASAPConnectionHandler bobConnectionHandler = (ASAPConnectionHandler) bob.getASAPPeer();

        ASAPEncounterManager aliceEncounterManager = new ASAPEncounterManagerImpl(aliceConnectionHandler);
        ASAPEncounterManager bobEncounterManager = new ASAPEncounterManagerImpl(bobConnectionHandler);

//        ////////////////////////// set up server socket and handle connection requests
        TCPServerSocketAcceptor aliceTcpServerSocketAcceptor =
                new TCPServerSocketAcceptor(portNumberAlice, aliceEncounterManager);

        TCPServerSocketAcceptor bobTcpServerSocketAcceptor =
                new TCPServerSocketAcceptor(portNumberBob, bobEncounterManager);

        // give it a moment to settle
        Thread.sleep(5);

        // now, both side wit for connection establishment. Example

        // open connection to Bob. This part should be replaced using the ASAPHub
        Socket socket = new Socket("localhost", portNumberBob);

        // let Alice handle it
        aliceEncounterManager.handleEncounter(
                StreamPairImpl.getStreamPair(socket.getInputStream(), socket.getOutputStream()),
                EncounterConnectionType.INTERNET);

        // give it a moment to run ASAP session and receive message
        Thread.sleep(2000);

        // bob reads message
        peerMessenger = (SharkMessengerComponent) bob.getComponent(SharkMessengerComponent.class);

        SharkMessengerChannel channel = peerMessenger.getChannel("my_channel/test");

        SharkMessageList list = channel.getMessages();
        String receivedMessage = new String(list.getSharkMessage(0, true).getContent(), StandardCharsets.UTF_8);

        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("Hi Bob", receivedMessage);
    }
}
