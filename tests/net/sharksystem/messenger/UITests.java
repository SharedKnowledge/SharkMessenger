package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandAddHubDescription;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandConnectHub;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandListConnectedHubs;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandListHubDescriptions;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandRemoveHubDescription;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandCreateChannel;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandGetMessageDetails;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandListChannels;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandListMessages;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandRemoveChannel;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandSendMessage;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandSetChannelAge;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandCreateCredentialMessage;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandCreateNewKeyPair;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandExchangeCertificates;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetCertificatesByIssuer;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetCertificatesBySubject;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetCertificationPath;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetIdentityAssurance;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetKeysCreationTime;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetNumberOfKnownPeers;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetOwnerInfo;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetSigningFailureRate;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandSetSigningFailureRate;
import net.sharksystem.cmdline.sharkmessengerUI.commands.test.UICommandCloseTCP;
import net.sharksystem.cmdline.sharkmessengerUI.commands.test.UICommandConnectTCP;
import net.sharksystem.cmdline.sharkmessengerUI.commands.test.UICommandExecuteCommands;
import net.sharksystem.cmdline.sharkmessengerUI.commands.test.UICommandOpenTCP;
import net.sharksystem.cmdline.sharkmessengerUI.commands.test.UICommandSendTestMessage;
import net.sharksystem.utils.fs.FSUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * Test class for easily reconstruct bugs found in interactive use of the SharkMessenger command line UI.
 * Command history of the interactive mode can be print to screen or saved to file and used here for setting up the
 * test szenario.
 * Those Commands can bet passed to the initializeSharkMessengerUI method as parameter to create a UI
 * Object with the required behaviour for each peer.
 *
 * For Szenarios where the correct alternating sequence of command execution of each peer is required the execution of
 * each command with the sharkMessengerUI.handleUserInput(command) might be the better solution.
 */
public class UITests {

    // It is probably better to place data storage directory inside playground directory but the sharkPeerFS class
    // doesn't allow to change that directory.
    private static final String TEST_DATA_STORAGE = "sharkMessengerDataStorage";
    private static final String EXECUTE_BATCH_AT_ONCE = "executeCommands false";
    private static final String ALICE = "alice";
    private static final String BOB = "bob";
    private static final String TEST_CHANNEL = "test://t1";

    private SharkMessengerUI initializeSharkMessengerUI(String peerName, String batchCommands)
            throws SharkException, IOException {
        SharkMessengerApp sharkMessengerApp = new SharkMessengerApp(peerName);
        SharkMessengerUI smUI = new SharkMessengerUI(batchCommands, System.in, System.out, System.err, sharkMessengerApp);

        //General
        //smUI.addCommand(new UICommandSaveLog(sharkMessengerApp, smUI, "saveLog", false));
        //smUI.addCommand(new UICommandShowLog(sharkMessengerApp, smUI, "showLog", false));
        //smUI.addCommand(new UICommandExit(sharkMessengerApp, smUI, "exit", false));

        // messages
        smUI.addCommand(new UICommandSendMessage(sharkMessengerApp, smUI, "sendMessage", true));
        smUI.addCommand(new UICommandListMessages(sharkMessengerApp, smUI, "listMessages", true));
        smUI.addCommand(new UICommandGetMessageDetails(sharkMessengerApp, smUI, "getMessageDetails", true));

        // channels
        smUI.addCommand(new UICommandListChannels(sharkMessengerApp, smUI, "lsChannel", true));
        smUI.addCommand(new UICommandCreateChannel(sharkMessengerApp, smUI, "mkChannel", true));
        smUI.addCommand(new UICommandSetChannelAge(sharkMessengerApp, smUI, "setChAge", true));
        smUI.addCommand(new UICommandRemoveChannel(sharkMessengerApp, smUI, "rmCh", true));

        //PKI
        smUI.addCommand(new UICommandGetOwnerInfo(sharkMessengerApp, smUI, "ownerInfo", true));
        smUI.addCommand(new UICommandGetNumberOfKnownPeers(sharkMessengerApp, smUI, "numPeers", true));
        smUI.addCommand(new UICommandCreateNewKeyPair(sharkMessengerApp, smUI, "mkKeys", true));
        smUI.addCommand(new UICommandGetKeysCreationTime(sharkMessengerApp, smUI, "keysTime", true));
        smUI.addCommand(new UICommandGetCertificatesByIssuer(sharkMessengerApp, smUI, "certByIssuer", true));
        smUI.addCommand(new UICommandGetCertificatesBySubject(sharkMessengerApp, smUI, "certBySubject", true));
        smUI.addCommand(new UICommandGetIdentityAssurance(sharkMessengerApp, smUI, "ia", true));
        smUI.addCommand(new UICommandGetSigningFailureRate(sharkMessengerApp, smUI, "getSF", true));
        smUI.addCommand(new UICommandSetSigningFailureRate(sharkMessengerApp, smUI, "setSF", true));
        smUI.addCommand(new UICommandCreateCredentialMessage(sharkMessengerApp, smUI, "mkCredentialMsg", true));
        smUI.addCommand(new UICommandExchangeCertificates(sharkMessengerApp, smUI, "exchCert", true));
        smUI.addCommand(new UICommandGetCertificationPath(sharkMessengerApp, smUI, "certPath", true));

        // Hub control
        smUI.addCommand(new UICommandListHubDescriptions(sharkMessengerApp, smUI, "lsHubDescr", true));
        smUI.addCommand(new UICommandAddHubDescription(sharkMessengerApp, smUI,"addHubDescr", true));
        smUI.addCommand(new UICommandRemoveHubDescription(sharkMessengerApp, smUI, "rmHubDescr", true));
        smUI.addCommand(new UICommandListConnectedHubs(sharkMessengerApp, smUI, "lsHubs", true));
        smUI.addCommand(new UICommandConnectHub(sharkMessengerApp, smUI, "connectHub", true));

        // Test
        smUI.addCommand(new UICommandOpenTCP(sharkMessengerApp, smUI, "openTCP", true));
        smUI.addCommand(new UICommandCloseTCP(sharkMessengerApp, smUI, "closeTCP", true));
        smUI.addCommand(new UICommandConnectTCP(sharkMessengerApp, smUI, "connectTCP", true));
        smUI.addCommand(new UICommandExecuteCommands(sharkMessengerApp, smUI, "executeCommands", false));
        smUI.addCommand(new UICommandSendTestMessage(sharkMessengerApp, smUI, "sendMessageTest", true));

        return smUI;
    }

    @BeforeEach
    public void resetStorageFolder() {
        // delete test dir if already exists
        FSUtils.removeFolder(TEST_DATA_STORAGE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         REPRODUCE BUGS WITH UI COMMAND LOG                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void reconstructIncomingChunkStorageBug() throws Exception {
        String cmdLogAlice = "openTCP 8889" +
                System.lineSeparator() +
                "mkChannel test://t1 channel1 false" +
                System.lineSeparator() +
                "sendMessage 0 false false hi_bob bob";

        String cmdLogBob = "connectTCP 8889 localhost" +
                System.lineSeparator() +
                "mkChannel test://t1 channel1 false";

        SharkMessengerUI smUIAlice = this.initializeSharkMessengerUI(ALICE, cmdLogAlice);
        SharkMessengerUI smUIBob = this.initializeSharkMessengerUI(BOB, cmdLogBob);

        // The Command execution is not alternating as in the original szenario but the chunk storage problem still can
        // be reconstructed.
        smUIAlice.handleUserInput(EXECUTE_BATCH_AT_ONCE);
        smUIBob.handleUserInput(EXECUTE_BATCH_AT_ONCE);

        // Give peers time to do peer things.
        Thread.sleep(1000);
    }

    @Test
    public void reconstructIncomingChunkStorageBugAlternatingExecution() throws Exception {
        String cmdAlice1st = "openTCP 8889";
        String cmdBob2nd = "connectTCP 8889 localhost";
        String cmdAlice3rd = "mkChannel test://t1 channel1 false";
        String cmdBob4th = "mkChannel test://t1 channel1 false";
        String cmdAlice5th = "sendMessage 0 false false hi_bob bob";

        SharkMessengerUI smUIAlice = this.initializeSharkMessengerUI(ALICE, "");
        SharkMessengerUI smUIBob = this.initializeSharkMessengerUI(BOB, "");

        smUIAlice.handleUserInput(cmdAlice1st);
        smUIBob.handleUserInput(cmdBob2nd);
        smUIAlice.handleUserInput(cmdAlice3rd);
        smUIBob.handleUserInput(cmdBob4th);
        smUIAlice.handleUserInput(cmdAlice5th);

        Thread.sleep(1000);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             TEST TOOL FUNCTIONALITY                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Verification files are used to control the test result in distributed tests. Each Peer outputs two files, one
     * with all information about all sent messages and one with all information about received messages. This test
     * checks whether those files are produced correctly.
     *
     * Send ten test messages from alice to bob and check if verification files are produced correctly.
     * Transmitted alice - received bob:
     *      The verificationTest_tx_alice.csv and verificationTest_rx_bob.csv must contain 11 entries.
     *      The verification files must be identical.
     *      The Entries must contain 1 header line and 10 lines with information about the exchanged messages with
     *       consecutive ID's from 0 to 9.
     * Transmitted bob - received alice:
     *      The verificationTest_rx_alice.csv and verificationTest_tx_bob.csv must only contain the header.
     * TODO: implement verification file creation
     */
    @Test
    public void produceVerificationFiles() throws Exception {

        // SETUP
        // csv files
        String testID = "verificationTest";
        // TODO: adapt path when verification files have been implemented
        String verPath = "";
        String txAlice = verPath + testID + "_" + "tx" + "_" + ALICE + ".csv";
        String rxAlice = verPath + testID + "_" + "rx" + "_" + ALICE + ".csv";
        String txBob = verPath + testID + "_" + "tx" + "_" + BOB + ".csv";
        String rxBob = verPath + testID + "_" + "rx" + "_" + BOB + ".csv";
        String csv_header = "sender,receiver,uri,id"; 
        String csvEntry = ALICE + "," + BOB + "," + TEST_CHANNEL;
        // commands
        int amountMessages = 10;
        String aliceOpenTCP = "openTCP 6666";
        String bobConnect2Alice = "connectTCP 6666 localhost";
        String makeChannel = "mkChannel " + TEST_CHANNEL + " channel1 false";
        String aliceSendTenMessages = "sendMessageTest " + amountMessages + " 0 0 false false hi_bob " + BOB;
        String makeVerificationFiles = "saveTestResults " + testID;
        // ui instances
        SharkMessengerUI smUIAlice = this.initializeSharkMessengerUI(ALICE, "");
        SharkMessengerUI smUIBob = this.initializeSharkMessengerUI(BOB, "");

        //TEST
        // message exchange
        smUIAlice.handleUserInput(aliceOpenTCP);
        smUIBob.handleUserInput(bobConnect2Alice);
        smUIAlice.handleUserInput(makeChannel);
        smUIBob.handleUserInput(makeChannel);
        smUIAlice.handleUserInput(aliceSendTenMessages);
        // give peers time to do their thing
        Thread.sleep(5000);
        // make verification files
        smUIAlice.handleUserInput(makeVerificationFiles);
        smUIBob.handleUserInput(makeVerificationFiles);

        // VERIFICATION
        // tx alice - rx bob
        BufferedReader brAlice = new BufferedReader(new InputStreamReader(new FileInputStream(txAlice)));
        BufferedReader brBob = new BufferedReader(new InputStreamReader(new FileInputStream(rxBob)));
        // check header
        Assertions.assertEquals(brAlice.readLine(), csv_header);
        Assertions.assertEquals(brBob.readLine(), csv_header);
        // check entries
        for (int i = 0; i < amountMessages ; i++) {
            Assertions.assertEquals( csvEntry + "," + i, brAlice.readLine());
            Assertions.assertEquals( csvEntry + "," + i, brBob.readLine());
        }
        // tx bob - rx alice
        brAlice = new BufferedReader(new InputStreamReader(new FileInputStream(rxAlice)));
        brBob = new BufferedReader(new InputStreamReader(new FileInputStream(txBob)));
        // check header
        Assertions.assertEquals(brAlice.readLine(), csv_header);
        Assertions.assertEquals(brBob.readLine(), csv_header);
        // check EOF
        Assertions.assertNull(brAlice.readLine());
        Assertions.assertNull(brBob.readLine());

    }

}
