package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPEncounterConnectionType;
import net.sharksystem.asap.ASAPEncounterManagerImpl;
import net.sharksystem.asap.ASAPPeerFS;
import net.sharksystem.asap.apps.TCPServerSocketAcceptor;
import net.sharksystem.utils.streams.StreamPairImpl;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class EncounterTests {
    private static final String ENCOUNTER_TEST = "encounterTest";

    @Test
    public void howToOpenAndConnectToPortAndStartEncounter() throws SharkException, IOException, InterruptedException {
        TestHelper testHelper = new TestHelper(ENCOUNTER_TEST);
        testHelper.setUpScenario_1();

        // create encounter manager for alice
        ASAPPeerFS aliceASAPPeerFS = testHelper.alicePeer.getASAPTestPeerFS();
        ASAPEncounterManagerImpl aliceEncounterManager =
                new ASAPEncounterManagerImpl(aliceASAPPeerFS, aliceASAPPeerFS.getPeerID());

        // create encounter manager for bob
        ASAPPeerFS bobASAPPeerFS = testHelper.bobPeer.getASAPTestPeerFS();
        ASAPEncounterManagerImpl bobEncounterManager =
                new ASAPEncounterManagerImpl(bobASAPPeerFS, bobASAPPeerFS.getPeerID());

        int alicePort = TestHelper.getPortNumber(); // in unit test always a good idea to choose a fresh port

        // offer a port on alice side - now alice would have an open port.
        new TCPServerSocketAcceptor(alicePort, aliceEncounterManager);

        ///////////////////////// Bob connects
        Socket connect2Alice = new Socket("localhost", alicePort);

        // handle to encounter manager on bob side
        bobEncounterManager.handleEncounter(
                StreamPairImpl.getStreamPair(
                        connect2Alice.getInputStream(), connect2Alice.getOutputStream(), net.sharksystem.utils.testsupport.TestConstants.ALICE_ID, net.sharksystem.utils.testsupport.TestConstants.ALICE_ID),
                ASAPEncounterConnectionType.INTERNET);

        // give it a moment to exchange data
        Thread.sleep(10);
        //Thread.sleep(Long.MAX_VALUE);
        System.out.println("slept a moment");
    }
}
