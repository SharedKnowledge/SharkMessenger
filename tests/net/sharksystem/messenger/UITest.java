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
import net.sharksystem.utils.fs.FSUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;


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
public class UITest {

    // It is probably better to place data storage directory inside playground directory but the sharkPeerFS class
    // doesn't allow to change that directory.
    private static final String TEST_DATA_STORAGE = "sharkMessengerDataStorage";
    private static final String ALICE = "alice";
    private static final String BOB = "bob";
    private static final String EXECUTE_BATCH_AT_ONCE = "executeCommands false";

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

        return smUI;
    }

    @BeforeEach
    public void resetStorageFolder() {
        FSUtils.removeFolder(TEST_DATA_STORAGE);
    }

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

}
